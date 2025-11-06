package anthonynguyen.showspace.config;

import com.google.gson.Gson;
import com.sys1yagi.mastodon4j.MastodonClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// Note: We shouldn't be hardcoding API keys

@Configuration
@PropertySource("classpath:mastodon-config.properties") // Loads from src/main/resources

public class MastodonConfig {

    @Value("${mastodon.instance.url}")
    private String instanceUrl;

    @Value("${mastodon.access.token}")
    private String accessToken;


    @Bean
    public MastodonClient mastodonClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        Gson gson = new Gson();

        return new MastodonClient.Builder(instanceUrl, okHttpClientBuilder, gson)
                .accessToken(accessToken)
                .build();
    }
}