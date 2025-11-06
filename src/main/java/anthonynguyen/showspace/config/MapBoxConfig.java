package anthonynguyen.showspace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:mapbox-config.properties")
public class MapBoxConfig {

    @Value("${mapbox.access.token}")
    private String mapboxAccessToken;

    public String getMapboxAccessToken() {
        return mapboxAccessToken;
    }
}