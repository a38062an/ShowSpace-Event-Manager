package anthonynguyen.showspace.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PageErrorController implements ErrorController {

	@RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object error = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int status = 0;
        if (statusCode != null) {
            status = Integer.parseInt(statusCode.toString());
            try {
                error = HttpStatus.valueOf(status).getReasonPhrase();
            } catch (IllegalArgumentException ex) {
                error = "Unexpected Error";
            }
            model.addAttribute("status", status);
            model.addAttribute("error", error);
        }

        model.addAttribute("path", requestUri != null ? requestUri.toString() : "Unknown");
        model.addAttribute("error", "We're sorry, the page you requested cannot be found.");
        return "error";
    }
}
