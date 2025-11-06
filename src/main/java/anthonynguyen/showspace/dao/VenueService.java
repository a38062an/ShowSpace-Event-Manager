package anthonynguyen.showspace.dao;

import java.time.LocalDate;
import java.util.List;

import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

public interface VenueService {

	public long count();

	public List<Venue> findAll();
	
	public List<Venue> findByNameContainingIgnoreCase(String name);
	
	public void save(Venue venue);

	public boolean existsById(long id);

    public void deleteById(long id);
    
    Venue findById(Long id);
    
    List<Event> findNextThreeEventsForVenue(long venueId);
    
    
    
	
}
