package anthonynguyen.showspace.dao;

import java.util.List;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

public interface HomeService {
    /**
     * Retrieve the top featured upcoming events
     *
     * @return list of featured events
     */
    List<Event> getFeaturedUpcomingEvents();

    /**
     * Retrieve the top venues by number of events
     * @param limit maximum number of venues to retrieve
     * @return list of top venues
     */
    List<Venue> getTopVenuesByEventCount(int limit);
}