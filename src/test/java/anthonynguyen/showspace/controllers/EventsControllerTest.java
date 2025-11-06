package anthonynguyen.showspace.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import anthonynguyen.showspace.config.MapBoxConfig;
import anthonynguyen.showspace.config.Security;
import anthonynguyen.showspace.dao.EventService;
import anthonynguyen.showspace.dao.MastodonService;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventsController.class)
@Import({ Security.class, MapBoxConfig.class })
public class EventsControllerTest {

	@Autowired
	private MockMvc mvc;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@MockBean
	private EventService eventService;

	@MockBean
	private VenueService venueService;
	
	@MockBean
	private MastodonService mastodonService;
	
	//GET TESTS

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event>emptyList());


		mvc.perform(get("/events").accept(MediaType.TEXT_HTML))
								  .andExpect(status().isOk())
								  .andExpect(view().name("events/index"))
								  .andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyNoInteractions(event);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(venue.getName()).thenReturn("Kilburn Building");
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(venue));

		when(event.getVenue()).thenReturn(venue);
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(event));

		mvc.perform(get("/events")
			.accept(MediaType.TEXT_HTML))
			.andExpect(status().isOk())
			.andExpect(view().name("events/index"))
			.andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();

	}

	@Test
	public void getEventNotFound() throws Exception {
		when(eventService.findById(99L)).thenReturn(null);
		
		mvc.perform(get("/events/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found")).andExpect(handler().methodName("getEvent"));
	}
	
	@Test
	public void getExistingEvent() throws Exception {
		when(event.getVenue()).thenReturn(venue);
		when(eventService.findById(1L)).thenReturn(event);
		
		mvc.perform(get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/event_details")).andExpect(handler().methodName("getEvent"));
		
		verify(eventService).findById(1L);
	}

	// POST TESTS

	// Testing add event works
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventMapping() throws Exception {
		// Verify the GET mapping for the add event form
		mvc.perform(get("/events/add"))
				.andExpect(status().isOk())
				.andExpect(view().name("events/add_event"))
				.andExpect(handler().methodName("showAddEventForm"));
	}

	// Tests that valid event is added and verifies .save() to make sure data gets to the service layer
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithValidData() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		// Prepare valid event data
		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}

	// Invalid data should return isOk because of validation added to events entity
	
	// Test for when there is no name
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithEmptyName() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		// Prepare invalid event data (e.g., empty name)
		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "") // Invalid name
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when there is no date
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithNoDate() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", "")
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when there is no venue
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithNoVenue() throws Exception {
		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}

	// Test for when venue not found
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventVenueNotFound() throws Exception {
		when(venueService.findById(1L)).thenReturn(null);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found"));
	}
	
	// Test for when name is over char limit
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithTooLongName() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "A".repeat(500))
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when name is exactly 256 characters - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithCharLimitName() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "A".repeat(256))
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when name is exactly 255 characters (just under char limit) - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithLongestAllowedName() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "A".repeat(255))
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for past date
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithPastDate() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", LocalDate.now().minusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for TODAY's date - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithTodayDate() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", LocalDate.now().toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for TOMORROW's date - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithTomorrowDate() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A valid event description")
						.param("date", LocalDate.now().plusDays(1).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));
		
		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for when description is empty
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithEmptyDescription() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));
		
		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for when description is over char limit
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithTooLongDescription() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A".repeat(750))
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when description is exactly 500 characters - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithCharLimitDescription() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A".repeat(500))
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/add_event")); // Expect the add_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when description is exactly 499 characters (just under char limit) - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithLongestAllowedDescription() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "A".repeat(499))
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for when time is empty
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testAddEventWithEmptyTime() throws Exception {
		// Mock a venue
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Valid Event Name")
						.param("description", "")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", "")
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));
		
		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}

	// Test that when we are user the test/add is forbidden "403"
	@Test
	@WithMockUser(roles = "USER")
	public void testAddEventByUnauthorizedUser() throws Exception {
		// Try to add event as a regular user
		mvc.perform(post("/events/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Unauthorized Event")
				)
				.andExpect(status().isForbidden());
	}

	// UPDATE TESTS

	// Testing that the update action maps to the correct view
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventMapping() throws Exception {
		// Mock an existing event
		when(eventService.findById(1L)).thenReturn(event);

		// Verify the GET mapping for the update event form
		mvc.perform(get("/events/1/edit"))
				.andExpect(status().isOk())
				.andExpect(view().name("events/update_event"))
				.andExpect(handler().methodName("showUpdateForm"));
	}


	// Tests that event gets updated with valid information as an administrator
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithValidData() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		// Perform update
		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}

	// Testing what happens when you set invalid data
	
	// Test for when there is no name
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithEmptyName() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		// Perform update with invalid data (e.g., empty name)
		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "") // Invalid name
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf()) // Add CSRF token
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when there is no date
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithNoDate() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", "")
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when there is no venue
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithNoVenue() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);
		
		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found"));

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when venue not found
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventVenueNotFound() throws Exception {
		// Mock an existing event
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(null);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found"));
	}

	// Testing what happens when there is no event found
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventNotFound() throws Exception {
		when(eventService.findById(1L)).thenReturn(null);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found"));
	}
	
	// Test for when name is over char limit
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithTooLongName() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "A".repeat(500))
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when name is exactly 256 characters - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithCharLimitName() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "A".repeat(256))
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for when name is exactly 255 characters (just under char limit) - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithLongestAllowedName() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "A".repeat(255))
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for past date
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithPastDate() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().minusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for TODAY's date - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithTodayDate() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}
	
	// Test for TOMORROW's date - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithTomorrowDate() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(1).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for when description is empty
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithEmptyDescription() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for when description is over char limit
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithTooLongDescription() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "A".repeat(750))
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}

	// Test for when description is exactly 500 characters - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithCharLimitDescription() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "A".repeat(500))
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isOk()) // Expect 200 OK since it returns the form with errors
				.andExpect(view().name("events/update_event")); // Expect the update_event view

		// Verify that save was NOT called
		verify(eventService, never()).save(any(Event.class));
	}

	// Test for when description is exactly 499 characters (just under char limit) - EDGE CASE
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithLongestAllowedDescription() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "A".repeat(499))
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}
	
	// Test for when time is empty
	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testUpdateEventWithEmptyTime() throws Exception {
		// Mock an existing event and venue
		when(eventService.findById(1L)).thenReturn(event);
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", "")
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that save was called
		verify(eventService).save(any(Event.class));
	}

	// Testing if you can update as a USER
	@Test
	@WithMockUser(roles = "USER")
	public void testUpdateEventByUnauthorizedUser() throws Exception {
		mvc.perform(post("/events/1/edit")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("name", "Updated Event Name")
						.param("description", "Updated event description")
						.param("date", LocalDate.now().plusDays(10).toString())
						.param("time", LocalTime.now().toString())
						.param("venue.id", "1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				)
				.andExpect(status().isForbidden());
	}

	// DELETE TEST

	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testDeleteEvent() throws Exception {
		// Mock an existing event
		when(eventService.existsById(1L)).thenReturn(true);

		// Perform the delete
		mvc.perform(delete("/events/1")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/events"));

		// Verify that delete was called
		verify(eventService).deleteById(1L);
	}

	@Test
	@WithMockUser(roles = "ADMINISTRATOR")
	public void testDeleteNonExistentEvent() throws Exception {
		when(eventService.findById(9999L)).thenReturn(null);

		mvc.perform(delete("/events/9999")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
				).andExpect(status().isNotFound())
				.andExpect(view().name("events/not_found"));

		verify(eventService, never()).deleteById(9999L);
	}

	@Test
	@WithMockUser(roles = "USER")
	public void testDeleteEventByUnauthorizedUser() throws Exception {
		mvc.perform(delete("/events/1")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
		).andExpect(status().isForbidden());
	}

}
