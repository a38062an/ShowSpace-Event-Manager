package anthonynguyen.showspace.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/ready")
    public String ready() {
        // Lightweight readiness endpoint — returns quickly if the app process is up.
        return "OK";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
