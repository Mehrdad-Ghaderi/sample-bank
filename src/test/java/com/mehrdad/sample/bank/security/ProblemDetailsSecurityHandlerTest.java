package com.mehrdad.sample.bank.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mehrdad.sample.bank.api.error.ProblemDetailsFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;

class ProblemDetailsSecurityHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ProblemDetailsSecurityHandler handler = new ProblemDetailsSecurityHandler(
            objectMapper,
            new ProblemDetailsFactory()
    );

    @Test
    void commenceWritesUnauthorizedProblemDetail() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/accounts");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.commence(request, response, new InsufficientAuthenticationException("Missing token"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(response.getHeader(HttpHeaders.WWW_AUTHENTICATE)).isEqualTo("Bearer");
        assertThat(body.path("type").asText()).isEqualTo("https://api.sample-bank.local/problems/authentication-required");
        assertThat(body.path("title").asText()).isEqualTo("Authentication required");
        assertThat(body.path("status").asInt()).isEqualTo(401);
        assertThat(body.path("detail").asText()).isEqualTo("A valid bearer token is required to access this resource.");
        assertThat(body.path("instance").asText()).isEqualTo("/api/v1/accounts");
        assertThat(extension(body, "errorCode").asText()).isEqualTo("AUTHENTICATION_REQUIRED");
        assertThat(extension(body, "timestamp").isMissingNode()).isFalse();
    }

    @Test
    void handleWritesForbiddenProblemDetail() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/admin");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Forbidden"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(body.path("type").asText()).isEqualTo("https://api.sample-bank.local/problems/access-denied");
        assertThat(body.path("title").asText()).isEqualTo("Access denied");
        assertThat(body.path("status").asInt()).isEqualTo(403);
        assertThat(body.path("detail").asText()).isEqualTo("You do not have permission to access this resource.");
        assertThat(body.path("instance").asText()).isEqualTo("/api/v1/admin");
        assertThat(extension(body, "errorCode").asText()).isEqualTo("ACCESS_DENIED");
        assertThat(extension(body, "timestamp").isMissingNode()).isFalse();
    }

    private JsonNode extension(JsonNode body, String fieldName) {
        if (body.has(fieldName)) {
            return body.path(fieldName);
        }

        return body.path("properties").path(fieldName);
    }
}
