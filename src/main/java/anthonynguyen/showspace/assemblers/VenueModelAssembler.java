package anthonynguyen.showspace.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import anthonynguyen.showspace.controllers.VenuesControllerApi;
import anthonynguyen.showspace.controllers.VenuesControllerApi;
import anthonynguyen.showspace.entities.Event;
import anthonynguyen.showspace.entities.Venue;

@Component
public class VenueModelAssembler implements RepresentationModelAssembler<Venue, EntityModel<Venue>> {

	@Override
    public EntityModel<Venue> toModel(Venue venue) {
        return EntityModel.of(venue, linkTo(methodOn(VenuesControllerApi.class).getVenue(venue.getId())).withSelfRel(),
        		linkTo(methodOn(VenuesControllerApi.class).getVenue(venue.getId())).withRel("venue"),
                linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withRel("venues"),
        		linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("events").withRel("events"),
        		linkTo(methodOn(VenuesControllerApi.class).getNextThreeEvents(venue.getId())).withRel("next3events"));
    }
}

