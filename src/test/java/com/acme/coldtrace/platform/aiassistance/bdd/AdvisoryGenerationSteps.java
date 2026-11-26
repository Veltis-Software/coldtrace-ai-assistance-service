package com.acme.coldtrace.platform.aiassistance.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "coldtrace.internal.service-key=isolated-http-ai-owner-service-key-at-least-32-characters",
      "coldtrace.ai.enabled=false",
      "spring.ai.model.chat=none"
    })
public class AdvisoryGenerationSteps {
  private static final String KEY = "isolated-http-ai-owner-service-key-at-least-32-characters";
  private static final String PLAN = "/internal/ai/v1/generations/incident-plan";

  @Autowired Environment environment;
  private int status;
  private Map<?, ?> body;

  private RestClient client() {
    return RestClient.builder()
        .baseUrl("http://127.0.0.1:" + environment.getRequiredProperty("local.server.port"))
        .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {})
        .build();
  }

  @When("the health endpoint is requested")
  public void health() {
    status = client().get().uri("/actuator/health").retrieve().toBodilessEntity().getStatusCode().value();
  }

  @When("an incident plan is requested with a browser token")
  public void browserToken() {
    status =
        client().post().uri(PLAN).header("Authorization", "Bearer browser-token")
            .body(Map.of("incidentContext", "{\"assetName\":\"Cold room\",\"temperature\":12}"))
            .retrieve().toBodilessEntity().getStatusCode().value();
  }

  @When("an incident plan is requested with the internal service key")
  public void internalKey() {
    body =
        client().post().uri(PLAN).header("X-Service-Key", KEY)
            .body(Map.of("incidentContext", "{\"assetName\":\"Cold room\",\"temperature\":12}"))
            .retrieve().body(Map.class);
  }

  @Then("the response status is {int}")
  public void responseStatus(int expected) {
    assertEquals(expected, status);
  }

  @Then("the failure is {string}")
  public void failure(String expected) {
    assertEquals(expected, body.get("failure"));
  }

  @And("no plan is generated")
  public void noPlan() {
    assertNull(body.get("generated"));
  }
}
