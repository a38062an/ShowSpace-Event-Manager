package anthonynguyen.showspace.config.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import anthonynguyen.showspace.dao.EventService;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

@Configuration
@Profile("test")
public class TestDataLoader {

	private final static Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			// Build and save test events and venues here.
			// The test database is configured to reside in memory, so must be initialized
			// every time.
			
			log.info("Initialising test database");
			
			Venue venue1 = new Venue();
            venue1.setName("TEST VENUE 1");
            venue1.setRoadName("1 Test Road");
            venue1.setPostcode("TE1 8OD");
            venue1.setCapacity(123);
            venueService.save(venue1);
            
            Venue venue2 = new Venue();
            venue2.setName("TEST VENUE 2");
            venue1.setRoadName("2 Test Road");
            venue1.setPostcode("TE2 8OD");
            venue2.setCapacity(9999);
            venueService.save(venue2);
            
            Venue venue3 = new Venue();
            venue3.setName("TEST VENUE 3");
            venue1.setRoadName("3 Test Road");
            venue1.setPostcode("TE3 8OD");
            venue3.setCapacity(38);
            venueService.save(venue3);
			
            
            
            Event event1 = new Event();
            event1.setName("EVENT ONE");
            event1.setDate(LocalDate.parse("2024-06-10"));
            event1.setTime(LocalTime.parse("12:15"));
            event1.setDescription("This is the description for EVENT ONE");
            event1.setVenue(venue1);
            eventService.save(event1);

            Event event2 = new Event();
            event2.setName("EVENT TWO");
            event2.setDate(LocalDate.parse("2025-12-16"));
            event2.setTime(LocalTime.parse("06:30"));
            event2.setDescription("This is the description for EVENT TWO");
            event2.setVenue(venue2);
            eventService.save(event2);

            Event event3 = new Event();
            event3.setName("EVENT THREE");
            event3.setDate(LocalDate.parse("2025-04-04"));
            event3.setTime(LocalTime.parse("18:00"));
            event3.setDescription("This is the description for EVENT THREE");
            event3.setVenue(venue3);
            eventService.save(event3);

		};
	}
}
