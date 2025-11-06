package anthonynguyen.showspace.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomeRepository extends CrudRepository<Event, Long> {

    List<Event> findTop3ByDateAfterOrderByDateAsc(LocalDate today);


    @Query(value = "SELECT v FROM Venue v " +
            "LEFT JOIN Event e ON v.id = e.venue.id " +
            "GROUP BY v.id, v.name " +
            "ORDER BY COUNT(e.id) DESC")
    List<Venue> findTopVenuesByEventCount(Pageable pageable);
}