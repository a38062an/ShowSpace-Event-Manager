package anthonynguyen.showspace.exceptions;

public class VenueHasEventsException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
    
	public VenueHasEventsException() {
        
		super("Venue cannot be deleted because it has associated events.");
    
	}
}
