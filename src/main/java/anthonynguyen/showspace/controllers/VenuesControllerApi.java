package anthonynguyen.showspace.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import anthonynguyen.showspace.assemblers.VenueModelAssembler;
import anthonynguyen.showspace.dao.VenueService;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;
import anthonynguyen.showspace.exceptions.VenueNotFoundException;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

    private static final String NOT_FOUND_MSG = "{ \"error\": \"%s\", \"id\": %d }";

    @Autowired
    private VenueService venueService;
    

    @Autowired
    private VenueModelAssembler venueAssembler;

    @ExceptionHandler(VenueNotFoundException.class)
    public ResponseEntity<?> venueNotFoundHandler(VenueNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format(NOT_FOUND_MSG, ex.getMessage(), ex.getId()));
    }

    @GetMapping("/{id}")
    public EntityModel<Venue> getVenue(@PathVariable("id") long id) {
    	Venue venue = venueService.findById(id);
    	if (venue == null)
    		throw new VenueNotFoundException(id);
    	return venueAssembler.toModel(venue);
    }

    @GetMapping("/{id}/next3events")
    public ResponseEntity<?> getNextThreeEvents(@PathVariable("id") long id) {
        Venue venue = venueService.findById(id);
        if (venue == null) {
            throw new VenueNotFoundException(id);
        }
        List<Event> nextThreeEvents = venueService.findNextThreeEventsForVenue(id);
        return ResponseEntity.ok(nextThreeEvents);
    }

    @GetMapping
    public CollectionModel<EntityModel<Venue>> getAllVenues() {
        return venueAssembler.toCollectionModel(venueService.findAll())
                .add(linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel());
    }

}
