package anthonynguyen.showspace.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

import java.time.LocalDate;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Override
    public List<Event> getFeaturedUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return homeRepository.findTop3ByDateAfterOrderByDateAsc(today);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> getTopVenuesByEventCount(int limit) {
        List<Venue> venues = homeRepository.findTopVenuesByEventCount(PageRequest.of(0, limit));
        // Eagerly initialize the events collection to avoid LazyInitializationException
        venues.forEach(venue -> venue.getEvents().size());
        return venues;
    }
}