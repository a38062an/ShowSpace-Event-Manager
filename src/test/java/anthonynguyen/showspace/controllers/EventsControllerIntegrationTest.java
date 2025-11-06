package anthonynguyen.showspace.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.util.regex.Matcher;

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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import anthonynguyen.showspace.ShowSpaceApplication;
import anthonynguyen.showspace.testutil.FormUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShowSpaceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class EventsControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	private static String CSRF_HEADER = "X-CSRF-TOKEN";
	private static String SESSION_KEY = "JSESSIONID";

	@LocalServerPort
	private int port;
	
	private int currentRows;

	private WebTestClient client;

	@BeforeEach
	public void setup() {
		currentRows = countRowsInTable("events");
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	public void testGetAllEvents() {
		client.get().uri("/events").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Upcoming Events"));
					assertThat(result.getResponseBody(), containsString("Past Events"));
					assertThat(result.getResponseBody(), containsString("EVENT ONE"));
					assertThat(result.getResponseBody(), containsString("EVENT TWO"));
					assertThat(result.getResponseBody(), containsString("EVENT THREE"));
				});
	}

	@Test
	public void getEventNotFound() {
		client.get().uri("/events/99").accept(MediaType.TEXT_HTML).exchange().expectStatus().isNotFound().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Event Not Found"));
					assertThat(result.getResponseBody(), containsString("99"));
				});
	}
	
	@Test
	public void getEventsWithSearch() {
		client.get().uri("/events?search=o").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Upcoming Events"));
					assertThat(result.getResponseBody(), containsString("Past Events"));
					assertThat(result.getResponseBody(), containsString("EVENT ONE"));
					assertThat(result.getResponseBody(), containsString("EVENT TWO"));
					assertThat(result.getResponseBody(), not(containsString("EVENT THREE")));
				});
	}
	
	@Test
	public void getEvent() {
		client.get().uri("/events/1").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("EVENT ONE"));
					assertThat(result.getResponseBody(), containsString("TEST VENUE 1"));
					assertThat(result.getResponseBody(), containsString("2024-06-10"));
					assertThat(result.getResponseBody(), containsString("12:15"));
					assertThat(result.getResponseBody(), containsString("This is the description for EVENT ONE"));
				});
	}
	
	@Test
	public void getAddEventFormSignedOut() {
		client.get().uri("/events/add").accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
				.expectHeader().value("Location", endsWith("/sign-in"));
	}

	@Test
	public void getAddEventFormSignedIn() {
		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().get().uri("/events/add")
				.accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("_csrf"));
					assertThat(result.getResponseBody(), containsString("Add Event"));
					assertThat(result.getResponseBody(), containsString("Event Name"));
					assertThat(result.getResponseBody(), containsString("Venue"));
					assertThat(result.getResponseBody(), containsString("Date"));
					assertThat(result.getResponseBody(), containsString("Time"));
					assertThat(result.getResponseBody(), containsString("Description"));
				});
	}
	
	@Test
	public void getUpdateEventFormSignedOut() {
		client.get().uri("/events/1/edit").accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
				.expectHeader().value("Location", endsWith("/sign-in"));
	}
	
	@Test
	public void getUpdateEventFormSignedIn() {
		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().get().uri("/events/1/edit")
				.accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("_csrf"));
					assertThat(result.getResponseBody(), containsString("Edit Event"));
					assertThat(result.getResponseBody(), containsString("EVENT ONE"));
					assertThat(result.getResponseBody(), containsString("TEST VENUE 1"));
					assertThat(result.getResponseBody(), containsString("2024-06-10"));
					assertThat(result.getResponseBody(), containsString("12:15"));
					assertThat(result.getResponseBody(), containsString("This is the description for EVENT ONE"));
				});
	}
	
	@Test
	public void postAddEventSignedOut() {
		String[] tokens = login();

		// Attempt to POST a valid event.
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("name", "New Event");
	    form.add("venue.id", "1");
	    form.add("date", "2030-03-20");
	    form.add("time", "15:30");
	    form.add("description", "This is a description of the new event");

		// We don't set the session ID, so have no credentials.
		// This should redirect to the error
		client.post().uri("/events/add").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).exchange().expectStatus().isForbidden(); // 403
//				.isFound().expectHeader().value("Location", containsString("/sign-in"));

		// Check nothing added to the database.
		assertThat(currentRows, equalTo(countRowsInTable("events")));
	}
	
	@Test
	public void postAddEventSignedIn() {
		String[] tokens = login();

		// Attempt to POST a valid event.
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("name", "New Event");
	    form.add("venue.id", "1");
	    form.add("date", "2030-03-20");
	    form.add("time", "15:30");
	    form.add("description", "This is a description of the new event");

		// The session ID cookie holds our login credentials.
		client.post().uri("/events/add").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/events"));

		// Check one row is added to the database.
		assertThat(currentRows + 1, equalTo(countRowsInTable("events")));
	}
	
	@Test
	public void deleteEventSignedOut() {
		// Should redirect to the sign-in page. 
		//Mo: I changed it so it throws a forbidden error
		client.delete().uri("/events/1").accept(MediaType.TEXT_HTML).exchange().expectStatus().isForbidden();
//				.expectHeader().value("Location", containsString("/sign-in"));

		// Check that nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("events")));
	}
	
	@Test
	public void deleteEventSignedIn() {
		String[] tokens = login();

		// The session ID cookie holds our login credentials.
		// And for a DELETE we have no body, so we pass the CSRF token in the headers.
		client.delete().uri("/events/1").accept(MediaType.TEXT_HTML).header(CSRF_HEADER, tokens[0])
				.cookie(SESSION_KEY, tokens[1]).exchange().expectStatus().isFound().expectHeader()
				.value("Location", endsWith("/events"));

		// Check that one row is removed from the database.
		assertThat(currentRows - 1, equalTo(countRowsInTable("events")));
	}
	
	@Test
	public void deleteEventNotFound() {
		String[] tokens = login();

		// The session ID cookie holds our login credentials.
		// And for a DELETE we have no body, so we pass the CSRF token in the headers.
		client.delete().uri("/events/99").accept(MediaType.TEXT_HTML).header(CSRF_HEADER, tokens[0])
				.cookie(SESSION_KEY, tokens[1]).exchange().expectStatus().isNotFound();

		// Check nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("events")));
	}
	
	private String[] login() {
		String[] tokens = new String[2];

		// Although this doesn't POST the log in form it effectively logs us in.
		// If we provide the correct credentials here, we get a session ID back which
		// keeps us logged in.
		EntityExchangeResult<String> result = client.mutate().filter(basicAuthentication("Rob", "Haines")).build().get()
				.uri("/").accept(MediaType.TEXT_HTML).exchange().expectBody(String.class).returnResult();
		tokens[0] = FormUtil.getCsrfToken(result.getResponseBody());
		tokens[1] = result.getResponseCookies().getFirst(SESSION_KEY).getValue();

		return tokens;
	}
}
