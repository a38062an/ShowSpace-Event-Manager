package anthonynguyen.showspace.dao;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import anthonynguyen.showspace.ShowSpaceApplication;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShowSpaceApplication.class)
@DirtiesContext
@ActiveProfiles("test")
//@Disabled
public class VenueServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private VenueService venueService;
	
    @MockBean
    private VenueRepository venueRepository;

    @MockBean
    private EventRepository eventRepository;
    
    @MockBean
    private EventService eventService; // For the next3events method

    @Test
    public void testCountReturnsCorrectValue() {
        when(venueRepository.count()).thenReturn(5L);
        long result = venueService.count();
        assertEquals(5L, result);
    }
    
    @Test
    void testFindAllReturnsListOfVenues() {
        // Arrange: Create mock venues
        Venue venue1 = new Venue();
        venue1.setName("FeinFeinFein");
        venue1.setPostcode("M14 6AB");
        venue1.setCapacity(100);
        venue1.setRoadName("22nd Fein-fomour");

        Venue venue2 = new Venue();
        venue2.setName("tspmofr");
        venue2.setPostcode("M15 4XY");
        venue2.setCapacity(200);
        venue2.setRoadName("14th Jefferey");

        List<Venue> venues = Arrays.asList(venue1, venue2);

        // Mock repository
        when(venueRepository.findAllByOrderByNameAsc()).thenReturn(venues);
        // Act
        List<Venue> result = venueService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("FeinFeinFein", result.get(0).getName());
        assertEquals("tspmofr", result.get(1).getName());
        // Verify repo method call
        verify(venueRepository).findAllByOrderByNameAsc();
    }

    @Test
    void testFindAllEmptyList() {
    	when(venueRepository.findAllByOrderByNameAsc()).thenReturn(List.of());
    	List<Venue> result = venueService.findAll();
    	assertTrue(result.isEmpty());
    }
    
    @Test
    void testSaveVenueCallsRepository() {
        // Arrange
        Venue venue = new Venue();
        venue.setName("Test Venue");
        venue.setPostcode("M1 1AA");
        venue.setCapacity(50);
        venue.setRoadName("Test Road");
        // Act
        venueService.save(venue);
        // Assert
        verify(venueRepository, times(1)).save(venue);
    }

    @Test
    void testExistsByIdReturnsTrue() {
        long id = 1L;
        when(venueRepository.existsById(id)).thenReturn(true);
        // Act
        boolean exists = venueService.existsById(id);
        // Assert
        assertTrue(exists);
        verify(venueRepository, times(1)).existsById(id);
    }

    @Test
    void testExistsByIdReturnsFalse() {
    	// Same pattern
        long id = 2L;
        when(venueRepository.existsById(id)).thenReturn(false);
        boolean exists = venueService.existsById(id);
        assertFalse(exists);
    }
 
    @Test
    void testDeleteById() {
        long id = 3L;
        venueService.deleteById(id);
        verify(venueRepository, times(1)).deleteById(id);
    }
 
    @Test
    void testDeleteByIdNonexistentId() {
    	doNothing().when(venueRepository).deleteById(999L);
    	venueService.deleteById(999L);
    	verify(venueRepository).deleteById(999L);
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
    	// Arrange
        Venue venue1 = new Venue();
        venue1.setName("University Hall");
        Venue venue2 = new Venue();
        venue2.setName("Tech Hall");
        
        // Act
        List<Venue> mockResults = List.of(venue1, venue2);
        when(venueRepository.findByNameContainingIgnoreCase("hall")).thenReturn(mockResults);
        List<Venue> result = venueService.findByNameContainingIgnoreCase("hall");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("hall"));
        assertTrue(result.get(1).getName().toLowerCase().contains("hall"));
        verify(venueRepository).findByNameContainingIgnoreCase("hall");
    }

    @Test
    void testFindByNameContainingIgnoreCaseReturnsEmpty() {
        when(venueRepository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());
        List<Venue> result = venueService.findByNameContainingIgnoreCase("xyz");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByNameContainingIgnoreCaseEmptyString(){
    	when(venueRepository.findByNameContainingIgnoreCase("")).thenReturn(List.of());
    	List<Venue> result = venueService.findByNameContainingIgnoreCase("");
    	assertTrue(result.isEmpty());
    }

    @Test
    void testFindNextThreeEventsForVenue() {
        long venueId = 1L;
        Event event1 = new Event();
        Event event2 = new Event();
        Event event3 = new Event();

        // Act        
        List<Event> mockEvents = List.of(event1, event2, event3);
        when(eventService.findNextThreeEventsByVenue(venueId)).thenReturn(mockEvents);
        List<Event> result = venueService.findNextThreeEventsForVenue(venueId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(mockEvents, result);
        verify(eventService, times(1)).findNextThreeEventsByVenue(venueId);
    }


    // is it possible to write unit tests for an external api call??

    

	// This class is here as a starter for testing any custom methods within the
	// VenueService. Note: It is currently @Disabled!
}
