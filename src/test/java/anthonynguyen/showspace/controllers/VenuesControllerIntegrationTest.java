package anthonynguyen.showspace.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

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
import org.springframework.web.reactive.function.BodyInserters;

import anthonynguyen.showspace.ShowSpaceApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShowSpaceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VenuesControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @BeforeEach
    public void setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }



    /* GET Tests */
    
    @Test
    public void testGetAllVenues() {
        client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
		.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
			assertThat(result.getResponseBody(), containsString("All Venues"));
			assertThat(result.getResponseBody(), containsString("TEST VENUE 1"));
			assertThat(result.getResponseBody(), containsString("TEST VENUE 2"));
			assertThat(result.getResponseBody(), containsString("TEST VENUE 3"));
		});
    }

    @Test
    public void getVenueNotFound() {
        client.get().uri("/venues/99").accept(MediaType.TEXT_HTML).exchange().expectStatus().isNotFound().expectHeader()
                .contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("Venue Not Found"));
                });
    }
    
    @Test
	public void getVenuesWithSearch() {
		client.get().uri("/venues?search=2").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
		.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
			assertThat(result.getResponseBody(), containsString("All Venues"));
			assertThat(result.getResponseBody(), not(containsString("TEST VENUE 1")));
			assertThat(result.getResponseBody(), containsString("TEST VENUE 2"));
			assertThat(result.getResponseBody(), not(containsString("TEST VENUE 3")));
		});
	}

    @Test
	public void getVenue() {
		client.get().uri("/venues/1").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
		.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
			assertThat(result.getResponseBody(), containsString("TEST VENUE 1"));
			assertThat(result.getResponseBody(), containsString("1 Test Road"));
			assertThat(result.getResponseBody(), containsString("TE1 8OD"));
			assertThat(result.getResponseBody(), containsString("123"));
		});
	}
    
    @Test
	public void getAddVenueFormSignedIn() {
		client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().get().uri("/venues/add")
				.accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("_csrf"));
					assertThat(result.getResponseBody(), containsString("Add Venue"));
					assertThat(result.getResponseBody(), containsString("Venue Name"));
					assertThat(result.getResponseBody(), containsString("Road Name"));
					assertThat(result.getResponseBody(), containsString("Postcode"));
					assertThat(result.getResponseBody(), containsString("Capacity"));
				});
	}
    
    @Test
	public void getUpdateVenueFormSignedIn() {
		client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().get().uri("/venues/1/edit")
				.accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("_csrf"));
					assertThat(result.getResponseBody(), containsString("Edit Venue"));
					assertThat(result.getResponseBody(), containsString("TEST VENUE 1"));
					assertThat(result.getResponseBody(), containsString("1 Test Road"));
					assertThat(result.getResponseBody(), containsString("T11 ROD"));
					assertThat(result.getResponseBody(), containsString("123"));
				});
	}
    
    @Test
	public void getAddVenueFormSignedOut() {
		client.get().uri("/venues/add").accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
  				.expectHeader().value("Location", endsWith("/sign-in"));
  	}
    
    @Test
	public void getUpdateVenueFormSignedOut() {
		client.get().uri("/venues/1/edit").accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound()
				.expectHeader().value("Location", endsWith("/sign-in"));
	}



    /* POST Tests */

    @Test
    public void postAddVenueFormSignedIn() {
        client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().post().uri("/venues/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("name", "TEST VENUE 4")
                        .with("roadName", "4 Test Road")
                        .with("postcode", "TE4 8OD")
                        .with("capacity", "500"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", containsString("/venues"));
				
        client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("TEST VENUE 4"));
                    assertThat(result.getResponseBody(), containsString("4 Test Road"));
                    assertThat(result.getResponseBody(), containsString("TE4 8OD"));
                    assertThat(result.getResponseBody(), containsString("500"));
                });
    }
	
	@Test
    public void postUpdateVenueFormSignedIn() {
        client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().post().uri("/venues/1/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("name", "UPDATED TEST VENUE 1")
                        .with("roadName", "1 Updated Road")
                        .with("postcode", "UP6 4TE")
                        .with("capacity", "999"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", endsWith("/venues"));
				
        client.get().uri("/venues/1").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("UPDATED TEST VENUE 1"));
                    assertThat(result.getResponseBody(), containsString("1 Updated Road"));
                    assertThat(result.getResponseBody(), containsString("UP6 4TE"));
                    assertThat(result.getResponseBody(), containsString("999"));
                });
    }

	@Test
    public void postAddVenueFormSignedOut() {
        client.post().uri("/venues")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("name", "UNAUTHORISED TEST VENUE")
                        .with("roadName", "1 Unauthorised Road")
                        .with("postcode", "H4C K3R")
                        .with("capacity", "17"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", containsString("/sign-in"));
				
        client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), not(containsString("UNAUTHORISED TEST VENUE")));
                    assertThat(result.getResponseBody(), not(containsString("1 Unauthorised Road")));
                });
    }
	
	@Test
    public void postUpdateVenueFormSignedOut() {
        client.post().uri("/venues/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("name", "UNAUTHORISED TEST VENUE")
                        .with("roadName", "1 Unauthorised Road")
                        .with("postcode", "H4C K3D")
                        .with("capacity", "17"))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", containsString("/sign-in"));
				
        client.get().uri("/venues/1").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), not(containsString("UNAUTHORISED TEST VENUE")));
                    assertThat(result.getResponseBody(), not(containsString("1 Unauthorised Road")));
                });
    }

	@Test
    public void postUpdateVenueNotFound() {
        client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().post().uri("/venues/99")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("name", "NON-EXISTENT TEST VENUE")
                        .with("roadName", "1 Nonexistent Road")
                        .with("postcode", "NO7 3ND")
                        .with("capacity", "0"))
                .exchange()
                .expectStatus().isNotFound();
    }
	


	/* DELETE TESTS */
	
	@Test
    public void deleteVenueFormSignedIn() {
        client.get().uri("/venues/3").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("TEST VENUE 3"));
                });
				
        client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().delete().uri("/venues/3")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", endsWith("/venues"));
				
        client.get().uri("/venues/3").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isNotFound();
				
        client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), not(containsString("TEST VENUE 3")));
                });
    }
	
	@Test
    public void deleteVenueFormSignedOut() {
        client.delete().uri("/venues/3")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", endsWith("/sign-in"));
				
        client.get().uri("/venues/3").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("TEST VENUE 3"));
                });
    }
	
	@Test
    public void deleteVenueNotFound() {
        client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().delete().uri("/venues/99")
                .exchange()
                .expectStatus().isNotFound();
    }
	
	@Test
    public void deleteVenueWithAssociatedEvents() {
        client.mutate().filter(basicAuthentication("Markel", "Vigo")).build().delete().uri("/venues/1")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().value("Location", endsWith("/venues"));
				
        client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("Venue cannot be deleted because it has upcoming events."));
                });
				
        client.get().uri("/venues").accept(MediaType.TEXT_HTML).exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody(), containsString("TEST VENUE 1"));
                });
    }
}