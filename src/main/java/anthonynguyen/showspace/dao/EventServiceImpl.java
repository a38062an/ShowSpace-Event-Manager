package anthonynguyen.showspace.dao;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import anthonynguyen.showspace.entities.Event;

import java.time.LocalDate;

@Service
public class EventServiceImpl implements EventService {

	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);
	
	@Autowired
	private EventRepository eventRepository;

	@Override
	public long count() {
		return eventRepository.count();
	}
	
	@Override
	public List<Event> findAll() {
		return eventRepository.findAllByOrderByDateAscTimeAsc();
	}
	
	@Override
	@Transactional
	public Event save(Event event) {
		return eventRepository.save(event);

	}

	@Override
    public boolean existsById(long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public void deleteById(long id) {
        eventRepository.deleteById(id);
    }


	@Override
	public List<Event> findByNameContainingIgnoreCase(String name){
		return eventRepository.findByNameContainingIgnoreCase(name);
	}

    @Override
    public List<Event> findUpcomingEvents(LocalDate today) {
        return eventRepository.findByDateAfterOrDateOrderByDateAscNameAsc(today, today);
    }

    @Override
    public List<Event> findPastEvents(LocalDate today) {
        return eventRepository.findByDateBeforeOrderByDateDescNameAsc(today);
    }

    @Override
    public Event findById(Long id) {
    	log.info("Fetching event with ID: " + id);
    	return eventRepository.findById(id).orElse(null);
    }
    
    // Exceptions should be handled by the controller
    public List<Event> findUpcomingEventsByVenue(long venueId, LocalDate today) {
        // Ensure that the query here works and returns events
        return eventRepository.findUpcomingEventsByVenue(venueId, today);
    }
    
    public boolean existsByVenueId(long venueId) {
        return eventRepository.existsByVenueId(venueId);
    }
 
    
    @Override
    public boolean hasEvents(long venueid) {
    	return eventRepository.existsByVenueId(venueid);
    }

	@Override
	public boolean existsByVenueIdAndDateAfter(long venueId, LocalDate date) {
		return false;
	}
	
	@Override
	public List<Event> findNextThreeEventsByVenue(long venueId) {
	    LocalDate today = LocalDate.now();
	    return eventRepository.findTop3ByVenueIdAndDateAfterOrderByDateAsc(venueId, today);
	}
}
	

