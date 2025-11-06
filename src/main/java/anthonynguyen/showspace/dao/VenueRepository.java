package anthonynguyen.showspace.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

@Repository
public interface VenueRepository extends CrudRepository<Venue, Long> {

	List<Venue> findAllByOrderByNameAsc();
	
	List<Venue> findByNameContainingIgnoreCase(String name);

}
