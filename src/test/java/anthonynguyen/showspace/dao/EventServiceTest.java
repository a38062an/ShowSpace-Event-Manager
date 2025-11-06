package anthonynguyen.showspace.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.anyLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import anthonynguyen.showspace.ShowSpaceApplication;
import anthonynguyen.showspace.entities.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShowSpaceApplication.class)
@DirtiesContext
@ActiveProfiles("test")
//@Disabled
public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private EventService eventService;

	@MockBean
	private EventRepository eventRepository;

	@BeforeEach
	void setUp() {
	}

	// This class is here as a starter for testing any custom methods within the
	// EventService. Note: It is currently @Disabled!

	@Test
	void testFindAllOrderByDateAsc() {
		// Arrange: Create some mock events
		Event event1 = new Event();
		event1.setName("Event 1");
		event1.setDate(LocalDate.parse("2025-02-14"));
		event1.setTime(LocalTime.parse("11:00"));

		Event event2 = new Event();
		event2.setName("Event 2");
		event2.setDate(LocalDate.parse("2025-02-16"));
		event2.setTime(LocalTime.parse("15:00"));

		// Mock repository return value
		List<Event> events = Arrays.asList(event1, event2);
		when(eventRepository.findAllByOrderByDateAscTimeAsc()).thenReturn(events);

		// Act: Call the service method
		Iterable<Event> result = eventService.findAll();

		// Assert: Verify the result and ensure the repository method was called
		assertNotNull(result);
		assertEquals(2, ((Collection<Event>) result).size());
		verify(eventRepository).findAllByOrderByDateAscTimeAsc();
	}
	
	@Test
	public void testCount() {
		when(eventRepository.count()).thenReturn(5L);
		assertEquals(5, eventService.count());
	}
	
	@Test
	public void testSave() {
		when(eventRepository.save(any(Event.class))).thenReturn(new Event());
		Event saved = eventService.save(new Event());
		assertNotNull(saved);
	}
	
	@Test
	public void testExistsById() {
		when(eventRepository.existsById(1L)).thenReturn(true);
		assertTrue(eventService.existsById(1L));
	}
	
	@Test
	public void testDeleteById() {
		eventService.deleteById(1L);
		verify(eventRepository).deleteById(1L);
	}
	
	@Test
	public void testFindByNameContainingIgnoreCase() {
		when(eventRepository.findByNameContainingIgnoreCase("test")).thenReturn(List.of(new Event()));
		assertFalse(eventService.findByNameContainingIgnoreCase("test").isEmpty());
	}
	
	@Test
	public void testFindUpcomingEvents() {
		LocalDate today = LocalDate.now();
		when(eventRepository.findByDateAfterOrDateOrderByDateAscNameAsc(today, today)).thenReturn(List.of(new Event()));
		assertEquals(1, eventService.findUpcomingEvents(today).size());
	}
	
	@Test
	public void testFindPastEvents() {
		LocalDate today = LocalDate.now();
		when(eventRepository.findByDateBeforeOrderByDateDescNameAsc(today)).thenReturn(List.of(new Event()));
		assertEquals(1, eventService.findPastEvents(today).size());
	}
	
	@Test
	public void testFindById() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(new Event()));
		assertNotNull(eventService.findById(1L));
	}
	
	@Test
	public void testFindUpcomingEventsByVenue() {
		when(eventRepository.findUpcomingEventsByVenue(1L, LocalDate.now())).thenReturn(List.of(new Event()));
		assertEquals(1, eventService.findUpcomingEventsByVenue(1L, LocalDate.now()).size());
	}
	
	@Test
	public void testExistsByVenueId() {
		when(eventRepository.existsByVenueId(1L)).thenReturn(true);
		assertTrue(eventService.existsByVenueId(1L));
	}
	
	@Test
	public void testHasUpcomingEvents() {
		when(eventRepository.existsByVenueIdAndDateAfter(anyLong(), any(LocalDate.class))).thenReturn(true);
		assertTrue(eventService.hasEvents(1L));
	}
	
	@Test
	public void testFindNextThreeEventsByVenue() {
		when(eventRepository.findTop3ByVenueIdAndDateAfterOrderByDateAsc(anyLong(), any(LocalDate.class)))
			.thenReturn(List.of(new Event()));
		assertEquals(1, eventService.findNextThreeEventsByVenue(1L).size());
	}

}
