package anthonynguyen.showspace.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    public List<Venue> getTopVenuesByEventCount(int limit) {
        return homeRepository.findTopVenuesByEventCount(PageRequest.of(0, limit));
    }
}