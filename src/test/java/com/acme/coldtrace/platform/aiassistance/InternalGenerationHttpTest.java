package com.acme.coldtrace.platform.aiassistance;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties={
    "coldtrace.internal.service-key=isolated-http-ai-owner-service-key-at-least-32-characters",
    "coldtrace.ai.enabled=false", "spring.ai.model.chat=none"})
class InternalGenerationHttpTest {
  private static final String KEY="isolated-http-ai-owner-service-key-at-least-32-characters";
  @Autowired Environment environment;
  private RestClient client() {
    return RestClient.builder().baseUrl("http://127.0.0.1:"+environment.getRequiredProperty("local.server.port"))
        .defaultStatusHandler(HttpStatusCode::isError,(request,response)->{}).build();
  }
  @Test void statelessServiceStartsWithoutADatabase() {
    assertEquals(200,client().get().uri("/actuator/health").retrieve().toBodilessEntity().getStatusCode().value());
  }
  @Test void browserTokenCannotInvokeTheProviderPort() {
    assertEquals(401,client().post().uri("/internal/ai/v1/generations/incident-plan")
        .header("Authorization","Bearer browser-token").body(Map.of("incidentContext","{}"))
        .retrieve().toBodilessEntity().getStatusCode().value());
  }
  @Test void disabledProviderReturnsAControlledFailureWithoutInventingAdvice() {
    var response=client().post().uri("/internal/ai/v1/generations/incident-plan")
        .header("X-Service-Key",KEY).body(Map.of("incidentContext","{}"))
        .retrieve().body(Map.class);
    assertNotNull(response);
    assertEquals("ProviderDisabled",response.get("failure"));
    assertNull(response.get("generated"));
  }
}
