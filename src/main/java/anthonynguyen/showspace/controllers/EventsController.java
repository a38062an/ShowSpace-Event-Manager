package anthonynguyen.showspace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.time.LocalDate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import anthonynguyen.showspace.entities.Venue;
import anthonynguyen.showspace.config.MapBoxConfig;
import anthonynguyen.showspace.dao.EventService;
import anthonynguyen.showspace.dao.MastodonService;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.exceptions.EventNotFoundException;


@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

    @Autowired
    private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private MastodonService mastodonService;

	@Autowired
	private MapBoxConfig mapBoxConfig;

	@ExceptionHandler(EventNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String eventNotFoundHandler(EventNotFoundException ex, Model model) {
		model.addAttribute("not_found_id", ex.getId());

		return "events/not_found";
	}
	
	@GetMapping
	public String getAllEvents(@RequestParam(value = "search", required = false) String search, Model model) {
		List<Event> events;
        LocalDate today = LocalDate.now();

		if (search != null && !search.isEmpty()) {
			events = eventService.findByNameContainingIgnoreCase(search);
		} else {
			events = eventService.findAll();
		}

		List<Event> upcomingEvents = eventService.findUpcomingEvents(today);
		List<Event> pastEvents = eventService.findPastEvents(today);
		
		upcomingEvents.retainAll(events);
		pastEvents.retainAll(events);
		
		model.addAttribute("events", events);
		model.addAttribute("search", search);
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("pastEvents", pastEvents);
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("mapToken", mapBoxConfig.getMapboxAccessToken());
        model.addAttribute("mastodonPosts", mastodonService.getHomeFeed());

		return "events/index";
	}

	@GetMapping("/{id}")
	public String getEvent(@PathVariable("id") long id, Model model) {
		Event event = eventService.findById(id);
		if (event == null) {
	        throw new EventNotFoundException(id);
	    }
		model.addAttribute("event", event);
		model.addAttribute("mapToken", mapBoxConfig.getMapboxAccessToken());

		return "events/event_details";
	}
	
	@PostMapping("/{id}")
	public String postToMastodon(@PathVariable("id") long id, @RequestParam("content") String content, Model model, 
			RedirectAttributes redirectAttrs) {
		
		Event event = eventService.findById(id);
		if (event == null) {
	        throw new EventNotFoundException(id);
	    }
		
		if (content.trim().isEmpty()) {
	        model.addAttribute("error", "Post content cannot be empty.");
	        model.addAttribute("event", event);
	        return "events/event_details";
		}
	    if (content.length() > 500) {
	        model.addAttribute("error", "Post content must be no more than 500 characters.");
	        model.addAttribute("event", event);
	        return "events/event_details";
	    }
	    
		// Mastodon server actually allows for posts of 1024 chars
	    // So hashtags will always be added with limit of 500

    	String post = content + " #" + event.getName().replaceAll("\\s+", "_") + " #showspace";
		
		mastodonService.createPost(post);
		redirectAttrs.addFlashAttribute("ok_message", content);
		
		return "redirect:/events/" + id;
	}
	
	// Add event
	@GetMapping("/add")
	public String showAddEventForm(Model model) {
	    model.addAttribute("event", new Event()); // Creating an empty event object
	    model.addAttribute("venues", venueService.findAll()); // Access to all sites
	    return "events/add_event"; // Return add_event.html
	}
	
	@PostMapping("/add")
	public String addEvent(@ModelAttribute @Valid Event event, BindingResult result, Model model) {
	    if (result.hasErrors()) {
	        model.addAttribute("venues", venueService.findAll()); // Reload the site
	        return "events/add_event"; //  Return to the error page
	    }

		// Fixing a test where we can add an event with a null venue so checking now
		Venue venue = venueService.findById(event.getVenue().getId());
		if (venue == null) {
			throw new EventNotFoundException(event.getId());
		}
	    eventService.save(event); // Save New Event
	    return "redirect:/events"; // Redirect to event list
	}
	
	// Updating event
	@GetMapping("/{id}/edit")
	public String showUpdateForm(@PathVariable("id") Long id, Model model) {
	    Event event = eventService.findById(id);
	    if (event == null) {
	        throw new EventNotFoundException(id);
	    }
	    model.addAttribute("event", event);
	    model.addAttribute("venues", venueService.findAll());
	    return "events/update_event";  // Return update_event.html
	}

	@PostMapping("/{id}/edit")
	public String updateEvent(@PathVariable("id") Long id,
	                          @ModelAttribute @Valid Event eventData,
	                          BindingResult result,
	                          Model model) {
	    // Fetch the existing event
	    Event event = eventService.findById(id); 
	    if (event == null) {
	        throw new EventNotFoundException(id);
	    }

		Venue venue = venueService.findById(eventData.getVenue().getId());
		if (venue == null) {
			throw new EventNotFoundException(id);
		}

	    if (result.hasErrors()) {
	        model.addAttribute("venues", venueService.findAll());  // Re-add venues if validation fails
	        return "events/update_event";  // Return form with errors
	    }

	    // Apply the updated event data
	    event.setName(eventData.getName());
	    event.setDate(eventData.getDate());
	    event.setTime(eventData.getTime());
	    event.setVenue(eventData.getVenue());
	    event.setDescription(eventData.getDescription());

	    eventService.save(event); // Save the updated event

	    return "redirect:/events";  // Redirect to events list
	}

	@DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable("id") long id) {
        if (!eventService.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventService.deleteById(id);
        return "redirect:/events";
    }
}