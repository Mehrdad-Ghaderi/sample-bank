package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.auth.LoginRequest;
import com.mehrdad.sample.bank.api.error.ProblemDetailsFactory;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import com.mehrdad.sample.bank.security.DatabaseUserDetailsService;
import com.mehrdad.sample.bank.security.JwtService;
import com.mehrdad.sample.bank.security.LoginAttemptService;
import com.mehrdad.sample.bank.security.ProblemDetailsSecurityHandler;
import com.mehrdad.sample.bank.security.RevokedAccessTokenService;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import com.mehrdad.sample.bank.security.exception.TooManyLoginAttemptsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        SpringSecurityConfiguration.class,
        DatabaseUserDetailsService.class,
        JwtService.class,
        ProblemDetailsFactory.class,
        ProblemDetailsSecurityHandler.class
})
class AuthControllerWebMvcTest {

    private static final String LOGIN_PATH = ApiPaths.API_BASE_PATH + ApiPaths.AUTH + "/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LoginAttemptService loginAttemptService;

    @MockitoBean
    private RevokedAccessTokenService revokedAccessTokenService;

    @Test
    void loginReturnsBearerToken() throws Exception {
        LoginRequest request = new LoginRequest("user", "pass");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(activeUser("user", "pass")));

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value(not(blankOrNullString())))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    void loginRequiresUsername() throws Exception {
        LoginRequest request = new LoginRequest("", "pass");

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.detail").value("Request validation failed."))
                .andExpect(jsonPath("$.instance").value(LOGIN_PATH))
                .andExpect(jsonPath("$.violations[0].field").value("username"))
                .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));
    }

    @Test
    void loginRejectsInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("user", "wrong-pass");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(activeUser("user", "pass")));

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_LOGIN_CREDENTIALS"))
                .andExpect(jsonPath("$.detail").value("Username or password is incorrect."));

        verify(loginAttemptService).recordFailure("user");
    }

    @Test
    void loginRejectsRateLimitedUser() throws Exception {
        LoginRequest request = new LoginRequest("user", "pass");
        doThrow(new TooManyLoginAttemptsException(Instant.parse("2026-04-25T00:00:00Z")))
                .when(loginAttemptService).checkAllowed("user");

        mockMvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errorCode").value("TOO_MANY_LOGIN_ATTEMPTS"))
                .andExpect(jsonPath("$.retryAt").exists());
    }

    @Test
    void logoutRequiresAuthentication() throws Exception {
        mockMvc.perform(post(LOGIN_PATH.replace("/login", "/logout")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevokesCurrentAccessToken() throws Exception {
        mockMvc.perform(post(LOGIN_PATH.replace("/login", "/logout"))
                        .header("Authorization", TestJwtTokens.bearerToken()))
                .andExpect(status().isNoContent());

        verify(revokedAccessTokenService).revoke(any());
    }

    private static UserEntity activeUser(String username, String rawPassword) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(rawPassword));
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        return user;
    }
}
