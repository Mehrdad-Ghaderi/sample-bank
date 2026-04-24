package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.api.error.ProblemDetailsFactory;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import com.mehrdad.sample.bank.domain.service.UserService;
import com.mehrdad.sample.bank.security.DatabaseUserDetailsService;
import com.mehrdad.sample.bank.security.ProblemDetailsSecurityHandler;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        SpringSecurityConfiguration.class,
        DatabaseUserDetailsService.class,
        ProblemDetailsFactory.class,
        ProblemDetailsSecurityHandler.class
})
class UserControllerWebMvcTest {

    private static final String USERS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.USERS;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void createUserShouldRequireAuthentication() throws Exception {
        CreateUserRequest request = new CreateUserRequest("alice", "password123", UserRole.USER);

        mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createUserShouldRejectNonAdminUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("alice", "password123", UserRole.USER);

        mockMvc.perform(post(USERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verifyNoInteractions(userService);
    }

    @Test
    void createUserShouldReturnCreatedResponseForAdmin() throws Exception {
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CreateUserRequest request = new CreateUserRequest("alice", "password123", UserRole.ADMIN);
        UserResponse response = new UserResponse(
                userId,
                "alice",
                UserRole.ADMIN,
                true,
                Instant.parse("2026-04-23T15:30:00Z")
        );

        when(userService.createUser(request)).thenReturn(response);

        mockMvc.perform(post(USERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.adminBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/api/v1/users/" + userId))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value(true));
    }
}
