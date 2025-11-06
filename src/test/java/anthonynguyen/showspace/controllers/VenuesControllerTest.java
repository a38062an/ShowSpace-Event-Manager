package anthonynguyen.showspace.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import anthonynguyen.showspace.config.Security;
import anthonynguyen.showspace.dao.EventService;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.entities.Venue;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenueController.class)
@Import(Security.class)
public class VenuesControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private Venue venue;

    @MockBean
    private VenueService venueService;

    @MockBean
    private EventService eventService;

    // GET TESTS

    @Test
    public void getIndexWhenNoVenues() throws Exception {
        when(venueService.findAll()).thenReturn(Collections.<Venue>emptyList());

        mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                .andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

        verify(venueService).findAll();
        verifyNoInteractions(venue);
    }

    @Test
    public void getIndexWithVenues() throws Exception {
        when(venue.getName()).thenReturn("Kilburn Building");
        when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(venue));

        mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                .andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

        verify(venueService).findAll();
    }

    @Test
    public void getVenueNotFound() throws Exception {
        when(venueService.findById(99L)).thenReturn(null);

        mvc.perform(get("/venues/99").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
                .andExpect(view().name("venues/not_found")).andExpect(handler().methodName("getVenue"));
    }

    @Test
    public void getExistingVenue() throws Exception {
        when(venueService.findById(1L)).thenReturn(venue);

        mvc.perform(get("/venues/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                .andExpect(view().name("venues/venue_details")).andExpect(handler().methodName("getVenue"));

        verify(venueService).findById(1L);
    }

    // POST TESTS

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueMapping() throws Exception {
        mvc.perform(get("/venues/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("venues/add_venue"))
                .andExpect(handler().methodName("newVenueForm"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueWithValidData() throws Exception {
        mvc.perform(post("/venues/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Valid Venue Name")
                        .param("roadName", "Valid Road Name")
                        .param("postcode", "M13 9PL")
                        .param("capacity", "100")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/venues"));

        verify(venueService).save(any(Venue.class));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueWithEmptyName() throws Exception {
        mvc.perform(post("/venues/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("roadName", "Valid Road Name")
                        .param("postcode", "M13 9PL")
                        .param("capacity", "100")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("venues/add_venue"));

        verify(venueService, never()).save(any(Venue.class));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueWithTooLongName() throws Exception {
        mvc.perform(post("/venues/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "A".repeat(256)) // Exceeding the limit
                        .param("roadName", "Valid Road Name")
                        .param("postcode", "M13 9PL")
                        .param("capacity", "100")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("venues/add_venue"));

        verify(venueService, never()).save(any(Venue.class));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueWithTooLongRoadName() throws Exception {
        mvc.perform(post("/venues/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Valid Venue Name")
                        .param("roadName", "A".repeat(300)) // Exceeding the limit
                        .param("postcode", "M13 9PL")
                        .param("capacity", "100")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("venues/add_venue"));

        verify(venueService, never()).save(any(Venue.class));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueWithInvalidPostcode() throws Exception {
        mvc.perform(post("/venues/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Valid Venue Name")
                        .param("roadName", "Valid Road Name")
                        .param("postcode", "INVALID") // Invalid postcode
                        .param("capacity", "100")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("venues/add_venue"));

        verify(venueService, never()).save(any(Venue.class));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testAddVenueWithInvalidCapacity() throws Exception {
        mvc.perform(post("/venues/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Valid Venue Name")
                        .param("roadName", "Valid Road Name")
                        .param("postcode", "M13 9PL")
                        .param("capacity", "0") // Invalid capacity
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("venues/add_venue"));

        verify(venueService, never()).save(any(Venue.class));
    }

    // UPDATE TESTS

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testUpdateVenueMapping() throws Exception {
        when(venueService.findById(1L)).thenReturn(venue);

        mvc.perform(get("/venues/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("venues/update_venue"))
                .andExpect(handler().methodName("editVenueForm"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testUpdateVenueWithValidData() throws Exception {
        when(venueService.findById(1L)).thenReturn(venue);

        mvc.perform(post("/venues/1/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Updated Venue Name")
                        .param("roadName", "Updated Road Name")
                        .param("postcode", "M13 9PL")
                        .param("capacity", "200")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/venues"));

        verify(venueService).save(any(Venue.class));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testUpdateVenueWithEmptyName() throws Exception {
        when(venueService.findById(1L)).thenReturn(venue);

        mvc.perform(post("/venues/1/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("roadName", "Updated Road Name")
                        .param("postcode", "M13 9PL")
                        .param("capacity", "200")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("venues/update_venue"));

        verify(venueService, never()).save(any(Venue.class));
    }

    // DELETE TESTS

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testDeleteVenue() throws Exception {
        when(venueService.existsById(1L)).thenReturn(true);
        when(eventService.hasEvents(1L)).thenReturn(false);

        mvc.perform(delete("/venues/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/venues"));

        verify(venueService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    public void testDeleteNonExistentVenue() throws Exception {
        when(venueService.findById(9999L)).thenReturn(null);

        mvc.perform(delete("/venues/9999")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andExpect(status().isNotFound())
                .andExpect(view().name("venues/not_found"));

        verify(venueService, never()).deleteById(9999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteVenueByUnauthorizedUser() throws Exception {
        mvc.perform(delete("/venues/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andExpect(status().isForbidden());
    }
}