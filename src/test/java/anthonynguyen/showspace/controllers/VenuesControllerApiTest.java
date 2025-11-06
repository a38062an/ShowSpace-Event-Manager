package anthonynguyen.showspace.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import anthonynguyen.showspace.assemblers.VenueModelAssembler;
import anthonynguyen.showspace.config.Security;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenuesControllerApi.class)
@Import({ Security.class, VenueModelAssembler.class })
public class VenuesControllerApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VenueService venueService;

    @Test
    public void getIndexWhenNoVenues() throws Exception {
        when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

        mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));

        verify(venueService).findAll();
    }

    @Test
    public void getIndexWithVenues() throws Exception {
        Venue v = new Venue();
        v.setId(0);
        v.setName("Venue");
        v.setCapacity(100);
        v.setRoadName("Road");
        v.setPostcode("AB12 3CD");
        when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));

        mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
                .andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)))
                .andExpect(jsonPath("$._embedded.venues[0]._links.self.href", endsWith("/api/venues/0")))
                .andExpect(jsonPath("$._embedded.venues[0]._links.venue.href", endsWith("/api/venues/0")))
                .andExpect(jsonPath("$._embedded.venues[0]._links.events.href", endsWith("/api/venues/0/events")));


        verify(venueService).findAll();
    }

    @Test
    public void getVenueNotFound() throws Exception {
        mvc.perform(get("/api/venues/99").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("venue 99"))).andExpect(jsonPath("$.id", equalTo(99)))
                .andExpect(handler().methodName("getVenue"));
    }

    @Test
    public void getVenueById() throws Exception {
        Venue v = new Venue();
        v.setId(0);
        v.setName("Venue");
        v.setCapacity(100);
        v.setRoadName("Road");
        v.setPostcode("AB12 3CD");

        when(venueService.findById(0L)).thenReturn(v);

        mvc.perform(get("/api/venues/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getVenue"))
                .andExpect(jsonPath("$.name", equalTo("Venue")))
                .andExpect(jsonPath("$.capacity", equalTo(100)))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
                .andExpect(jsonPath("$._links.venue.href", endsWith("/api/venues/0")))
                .andExpect(jsonPath("$._links.events.href", endsWith("/api/venues/0/events")));

        verify(venueService).findById(0L);
    }

    @Test
    public void getNextThreeEvents() throws Exception {
        // Create a Venue object
        Venue v = new Venue();
        v.setId(0);
        v.setName("Venue");
        v.setCapacity(100);
        v.setRoadName("Road");
        v.setPostcode("AB12 3CD");

        // Create a list of 3 events for this venue
        Event e1 = new Event();
        e1.setId(1);
        e1.setName("Event 1");
        e1.setVenue(v);

        Event e2 = new Event();
        e2.setId(2);
        e2.setName("Event 2");
        e2.setVenue(v);

        Event e3 = new Event();
        e3.setId(3);
        e3.setName("Event 3");
        e3.setVenue(v);

        List<Event> events = List.of(e1, e2, e3);

        // Mock the venueService to return the venue and the list of events
        when(venueService.findById(0L)).thenReturn(v);
        when(venueService.findNextThreeEventsForVenue(0L)).thenReturn(events);

        // Perform the GET request and assert the response
        mvc.perform(get("/api/venues/0/next3events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getNextThreeEvents"))
                .andExpect(jsonPath("$.length()", equalTo(3))) // Ensure 3 events are returned
                .andExpect(jsonPath("$[0].name", equalTo("Event 1")))
                .andExpect(jsonPath("$[1].name", equalTo("Event 2")))
                .andExpect(jsonPath("$[2].name", equalTo("Event 3")));

        verify(venueService).findById(0L);
        verify(venueService).findNextThreeEventsForVenue(0L);
    }

    @Test
    public void getNextThreeEventsNotFound() throws Exception {
        // Mock the venueService to return null when trying to find the venue
        when(venueService.findById(99L)).thenReturn(null);

        // Perform the GET request and assert the response
        mvc.perform(get("/api/venues/99/next3events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("venue 99")))
                .andExpect(jsonPath("$.id", equalTo(99)))
                .andExpect(handler().methodName("getNextThreeEvents"));

        verify(venueService).findById(99L);
    }


}