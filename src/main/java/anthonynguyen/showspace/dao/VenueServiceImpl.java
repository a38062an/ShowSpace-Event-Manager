package anthonynguyen.showspace.dao;

//import java.io.InputStream;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import retrofit2.Response;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import anthonynguyen.showspace.entities.Event;

import anthonynguyen.showspace.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private EventService eventService;
	
	@Override
	public long count() {
		return venueRepository.count();
	}

	@Override
	public List<Venue> findAll() {
		return venueRepository.findAllByOrderByNameAsc();
	}
	
	@Override
	public List<Venue> findByNameContainingIgnoreCase(String name){
		return venueRepository.findByNameContainingIgnoreCase(name);
	}
	
	@Override
	@Transactional
	public void save(Venue venue) {
        // Only call API if address has changed to preserve API calls
		if (venue.isAddressChanged()) {
            double[] coordinates = getCoordinatesFromAddress(venue.getRoadName(), venue.getPostcode());
            venue.setLatitude(coordinates[0]);
            venue.setLongitude(coordinates[1]);
        }
        venueRepository.save(venue);
	}
	
	@Override
    public void deleteById(long id) {
        venueRepository.deleteById(id);
    }

    @Override
    public boolean existsById(long id) {
        return venueRepository.existsById(id);
    }
    
    @Override
    public Venue findById(Long id) {
        return venueRepository.findById(id).orElse(null);
    }

    @Override
    public List<Event> findNextThreeEventsForVenue(long venueId) {
        return eventService.findNextThreeEventsByVenue(venueId);
    }
    
    


    private double[] getCoordinatesFromAddress(String roadName, String postcode) {
        String address = roadName + ", " + postcode + ", UK";

        // Geocoding using Mapbox API
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoiYXFpYmZhcnVxdWkiLCJhIjoiY204ZWdwNmw4MDFiODJxcXd0cnAwc3JnOCJ9.10N0eEkD95aNtypda1RLnA")
                .query(address)
                .limit(1)  // Limit results to 1
                .build();

        try {
            // Execute geocoding request and get response
            Response<GeocodingResponse> response = mapboxGeocoding.executeCall();

            // Check if the response is successful and contains a body
            if (response.isSuccessful() && response.body() != null) {
                GeocodingResponse geocodingResponse = response.body();

                // Check if we got any features (locations)
                List<CarmenFeature> features = geocodingResponse.features();
                if (features != null && !features.isEmpty()) {
                    CarmenFeature feature = features.get(0);
                    Point point = feature.center();
                    double latitude = point.latitude();
                    double longitude = point.longitude();
                    return new double[]{latitude, longitude};
                }
            }
        } catch (Exception e) {
            // Handle geocoding errors gracefully
        }

        // Return default coordinates if unable to find location
        return new double[]{0.0, 0.0};
    }

}
