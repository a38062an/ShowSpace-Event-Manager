package anthonynguyen.showspace.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle VenueHasEventsException and redirect with an error message
    @ExceptionHandler(VenueHasEventsException.class)
    public String handleVenueHasUpcomingEventsException(VenueHasEventsException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/venues"; // Redirect back to the venues page
    }


}
