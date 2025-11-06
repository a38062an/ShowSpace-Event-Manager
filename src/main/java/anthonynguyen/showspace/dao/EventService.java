package anthonynguyen.showspace.dao;

import anthonynguyen.showspace.entities.Event;
import java.time.LocalDate;
import java.util.List;


public interface EventService {

	public long count();

	public List<Event> findAll();
	
	public Event save(Event event);

	public boolean existsById(long id);

    public void deleteById(long id);

	public List<Event> findByNameContainingIgnoreCase(String name);

	List<Event> findUpcomingEvents(LocalDate today);

	List<Event> findPastEvents(LocalDate today);
    
    Event findById(Long id);
    
    public List<Event> findUpcomingEventsByVenue(long venueId, LocalDate date);
   
    boolean existsByVenueId(long venueId);
    
    boolean existsByVenueIdAndDateAfter(long venueId, LocalDate date);
    
    boolean hasEvents(long venueId);
    
    List<Event> findNextThreeEventsByVenue(long venueId);
}
    


