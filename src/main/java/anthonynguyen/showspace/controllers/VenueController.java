package anthonynguyen.showspace.controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import anthonynguyen.showspace.entities.Venue;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.dao.EventService;
import anthonynguyen.showspace.exceptions.VenueNotFoundException;
import anthonynguyen.showspace.exceptions.VenueHasEventsException;
import java.time.LocalDate;


@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenueController {

    private final static Logger log = LoggerFactory.getLogger(VenueController.class);

    @Autowired
    private VenueService venueService;
    
    @Autowired
    private EventService eventService;

    @ExceptionHandler(VenueNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String venueNotFoundHandler(VenueNotFoundException ex, Model model) {
        model.addAttribute("not_found_id", ex.getId());
        return "venues/not_found";
    }
   

    @GetMapping
    public String getAllVenues(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Venue> venues;
    	
        if (search != null && !search.isEmpty()) {
			venues = venueService.findByNameContainingIgnoreCase(search);
		} else {
			venues = venueService.findAll();
		}
        
    	model.addAttribute("venues", venues);
        return "venues/index"; 
    }

    @GetMapping("/{id}")
    public String getVenue(@PathVariable("id") long id, Model model) {
    	// If venue is null, throw a VenueNotFoundException
    	Venue venue = venueService.findById(id);
        if (venue == null) {
            throw new VenueNotFoundException(id);
        }

        // Fetch upcoming events linked to this venue
        LocalDate today = LocalDate.now();
        List<Event> upcomingEvents = new ArrayList<>();
        try {
            upcomingEvents = eventService.findUpcomingEventsByVenue(id, today);
        } catch (Exception e) {
            // Handle error gracefully
        }

        model.addAttribute("venue", venue);
        model.addAttribute("upcomingEvents", upcomingEvents);

        
        return "venues/venue_details";
    }


    @GetMapping("/add")
    public String newVenueForm(Model model) {
        model.addAttribute("venue", new Venue());
        return "venues/add_venue"; // Form to create a new venue
    }

    @PostMapping("/add")
    public String createVenue(@ModelAttribute @Valid Venue venue, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "venues/add_venue";
        }
        venueService.save(venue);
        return "redirect:/venues";
    }

    @GetMapping("/{id}/edit")
    public String editVenueForm(@PathVariable("id") long id, Model model) {
        Venue venue = venueService.findById(id);
        if (venue == null) {
            throw new VenueNotFoundException(id);
        }
        model.addAttribute("venue", venue);
        return "venues/update_venue"; // Form to edit an existing venue
    }

    @PostMapping("/{id}/edit")
    public String updateVenue(@PathVariable("id") long id, @ModelAttribute @Valid Venue venueData, BindingResult result, Model model) {
        Venue venue = venueService.findById(id);
        if (venue == null) {
            throw new VenueNotFoundException(id);
        }
        if (result.hasErrors()) {
            model.addAttribute("venue", venueData); // Keep user input
            return "venues/update_venue"; // Stay on the form if errors exist
        }

        // Check if the venue's address or postcode has changed
        if (venue.getRoadName() == null || venueData.getRoadName() == null ||
                !venue.getRoadName().trim().equalsIgnoreCase(venueData.getRoadName().trim()) ||
                venue.getPostcode() == null || venueData.getPostcode() == null ||
                !venue.getPostcode().trim().equalsIgnoreCase(venueData.getPostcode().trim())){

            log.info("address has changed");


            // Set the addressChanged flag to true
            venue.setAddressChanged(true);

            // Update the venue's road name and postcode
            venue.setRoadName(venueData.getRoadName());
            venue.setPostcode(venueData.getPostcode());
        }
        else {
            venue.setAddressChanged(false);
        }

        venue.setName(venueData.getName());
        venue.setRoadName(venueData.getRoadName());  // Use separate road name
        venue.setPostcode(venueData.getPostcode());  // Use separate postcode
        venue.setCapacity(venueData.getCapacity());
        venueService.save(venue);
        return "redirect:/venues";
    }
    
    

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMINISTRATOR')")
    public String deleteVenue(@PathVariable("id") long id) {
    	if (!venueService.existsById(id)) {
            throw new VenueNotFoundException(id);
        }
        
        // Block deletion if there are upcoming events
        if (eventService.hasEvents(id)) {
            throw new VenueHasEventsException();
        }

        // Proceed with deletion if no upcoming events exist
        venueService.deleteById(id);
        return "redirect:/venues";
    }
}
