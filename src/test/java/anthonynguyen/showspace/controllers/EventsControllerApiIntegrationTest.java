package anthonynguyen.showspace.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;

import anthonynguyen.showspace.ShowSpaceApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShowSpaceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class EventsControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;

	private WebTestClient client;

	@BeforeEach
	public void setup() {
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port + "/api").build();
	}

	@Test
	public void testGetAllEvents() {
		client.get().uri("/events").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$._embedded.events.length()").value(equalTo(3))
				.jsonPath("$._embedded.events[0].name").isEqualTo("EVENT ONE")
				.jsonPath("$._embedded.events[1].name").isEqualTo("EVENT THREE")
				.jsonPath("$._embedded.events[2].name").isEqualTo("EVENT TWO")
				.jsonPath("$._links.self.href").value(endsWith("/api/events"));
	}
	
	@Test
	public void getEvent() {
		client.get().uri("/events/1").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody()
				.jsonPath("$.name").isEqualTo("EVENT ONE")
				.jsonPath("$.description").isEqualTo("This is the description for EVENT ONE")
				.jsonPath("$.date").isEqualTo("2024-06-10")
				.jsonPath("$.time").isEqualTo("12:15:00")
				.jsonPath("$._links.self.href").value(endsWith("/api/events/1"))
				.jsonPath("$._links.event.href").value(endsWith("/api/events/1"))
				.jsonPath("$._links.venue.href").value(endsWith("/api/events/1/venue"));
	}
	
	@Test
	public void getEventNotFound() {
		client.get().uri("/events/99").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNotFound()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.error")
				.value(containsString("event 99")).jsonPath("$.id").isEqualTo(99);
	}
}
