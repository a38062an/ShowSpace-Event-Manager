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
@Profile("default")
public class InitialDataLoader {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			if (venueService.count() > 0 || eventService.count() > 0) {
				log.info("Database already populated with venues/events. Skipping initialization.");
			} else {
				// Build and save initial venues here.
				Venue venue1 = new Venue();
	            venue1.setName("Kilburn 2.25");
	            venue1.setRoadName("23 Manchester Road");
	            venue1.setPostcode("E14 3BD");
	            venue1.setCapacity(120);
//	            venue1.setLatitute(50.0);
//	            venue1.setLongitude(50.0);
	            venueService.save(venue1);
	            
	            Venue venue2 = new Venue();
	            venue2.setName("Megalab");
	            venue2.setRoadName("Highland Road");
	            venue2.setPostcode("S43 2EZ");
	            venue2.setCapacity(500);
	            venueService.save(venue2);
	            
	            Venue venue3 = new Venue();
	            venue3.setName("Online");
	            venue3.setRoadName("19 Acacia Avenue");
	            venue3.setPostcode("WA15 8QY");
	            venue3.setCapacity(100000);
	            venueService.save(venue3);
			
				// Build and save initial events here.				
	            Event event1 = new Event();
	            event1.setName("Group H06 1");
	            event1.setDate(LocalDate.parse("2025-02-14"));
	            event1.setTime(LocalTime.parse("11:00"));
	            event1.setVenue(venue1);
	            eventService.save(event1);

	            Event event2 = new Event();
	            event2.setName("Group H06 2");
	            event2.setDate(LocalDate.parse("2025-02-16"));
	            event2.setTime(LocalTime.parse("11:30"));
	            event2.setVenue(venue1);
	            eventService.save(event2);

	            Event event3 = new Event();
	            event3.setName("Group H06 3");
	            event3.setDate(LocalDate.parse("2025-02-20"));
	            event3.setTime(LocalTime.parse("15:00"));
	            event3.setVenue(venue1);
	            eventService.save(event3);

	            // Testing past/upcoming events by search - Issue #16
	            Event event4 = new Event();
	            event4.setName("Event 4");
	            event4.setDate(LocalDate.parse("2025-03-01"));
	            event4.setTime(LocalTime.parse("16:00"));
	            event4.setVenue(venue2);
	            eventService.save(event4);
	            
	            Event event5 = new Event();
	            event5.setName("Event 5");
	            event5.setDate(LocalDate.parse("2025-03-02"));
	            event5.setTime(LocalTime.parse("10:30"));
	            event5.setVenue(venue3);
	            eventService.save(event5);

	            Event event6 = new Event();
	            event6.setName("Event 6");
	            event6.setDate(LocalDate.parse("2025-05-04"));
	            event6.setTime(LocalTime.parse("12:00"));
	            event6.setVenue(venue2);
	            eventService.save(event6);
	            
	            Event event7 = new Event();
	            event7.setName("Event 7");
	            event7.setDate(LocalDate.parse("2025-05-18"));
	            event7.setTime(LocalTime.parse("10:00"));
	            event7.setVenue(venue3);
	            eventService.save(event7);
	               
			}
		};
	}
}
