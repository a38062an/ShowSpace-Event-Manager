package anthonynguyen.showspace.exceptions;

public class VenueHasUpcomingEventsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public VenueHasUpcomingEventsException() {
        super("Venue cannot be deleted because it has upcoming events.");
    }
}
