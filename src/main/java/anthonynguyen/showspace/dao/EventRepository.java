package anthonynguyen.showspace.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import anthonynguyen.showspace.entities.Event;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
	
	List<Event> findAllByOrderByDateAscTimeAsc();

	List<Event> findByNameContainingIgnoreCase(String name);
    
	List<Event> findByDateAfterOrDateOrderByDateAscNameAsc(LocalDate today, LocalDate todayAgain);

	List<Event> findByDateBeforeOrderByDateDescNameAsc(LocalDate today);

    @Query("SELECT e FROM Event e WHERE e.venue.id = :venueId AND e.date >= :today ORDER BY e.date ASC")
    List<Event> findUpcomingEventsByVenue(@Param("venueId") long venueId, @Param("today") LocalDate today);

    boolean existsByVenueId(long venueId);
    
    boolean existsByVenueIdAndDateAfter(long venueId, LocalDate currentDate);
    
    List<Event> findTop3ByVenueIdAndDateAfterOrderByDateAsc(long venueId, LocalDate date);

}

