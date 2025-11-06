package anthonynguyen.showspace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import anthonynguyen.showspace.dao.HomeService;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(produces = { MediaType.TEXT_HTML_VALUE })
public class HomeController {

    @Autowired
    private HomeService homeService;


    @GetMapping("/")
    public String home(Model model) {
        // Fetch the next 3 upcoming events
        List<Event> featuredEvents = homeService.getFeaturedUpcomingEvents();
        model.addAttribute("featuredEvents", featuredEvents);

        // Fetch top 3 venues by event count
        List<Venue> topVenues = homeService.getTopVenuesByEventCount(3);
        model.addAttribute("topVenues", topVenues);

        //return "redirect:/home.html";
        return "home";
    }

}