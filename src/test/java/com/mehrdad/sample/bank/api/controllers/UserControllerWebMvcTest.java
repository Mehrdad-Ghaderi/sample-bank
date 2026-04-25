package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.user.CreateUserRequest;
import com.mehrdad.sample.bank.api.dto.user.UserResponse;
import com.mehrdad.sample.bank.api.error.ProblemDetailsFactory;
import com.mehrdad.sample.bank.domain.entity.UserRole;
import com.mehrdad.sample.bank.domain.exception.user.UserAlreadyDisabledException;
import com.mehrdad.sample.bank.domain.exception.user.UserNotFoundException;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

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
        CreateUserRequest request = new CreateUserRequest("alice", "password123", UserRole.ADMIN);
        UserResponse response = new UserResponse(
                USER_ID,
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
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/api/v1/users/" + USER_ID))
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    void getUsersShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsersShouldRejectNonAdminUser() throws Exception {
        mockMvc.perform(get(USERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verifyNoInteractions(userService);
    }

    @Test
    void getUsersShouldReturnPagedResponseForAdmin() throws Exception {
        UserResponse response = new UserResponse(
                USER_ID,
                "alice",
                UserRole.ADMIN,
                true,
                Instant.parse("2026-04-23T15:30:00Z")
        );
        when(userService.getUsers(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(response)));

        mockMvc.perform(get(USERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.adminBearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.content[0].username").value("alice"))
                .andExpect(jsonPath("$.content[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.content[0].enabled").value(true));
    }

    @Test
    void enableUserShouldRequireAdmin() throws Exception {
        mockMvc.perform(patch(USERS_PATH + "/" + USER_ID + "/enable")
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verifyNoInteractions(userService);
    }

    @Test
    void enableUserShouldReturnNoContentForAdmin() throws Exception {
        mockMvc.perform(patch(USERS_PATH + "/" + USER_ID + "/enable")
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.adminBearerToken()))
                .andExpect(status().isNoContent());

        verify(userService).enableUser(USER_ID);
    }

    @Test
    void disableUserShouldReturnProblemWhenUserAlreadyDisabled() throws Exception {
        doThrow(new UserAlreadyDisabledException(USER_ID)).when(userService).disableUser(USER_ID);

        mockMvc.perform(patch(USERS_PATH + "/" + USER_ID + "/disable")
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.adminBearerToken()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("USER_ALREADY_DISABLED"));
    }

    @Test
    void disableUserShouldReturnProblemWhenUserMissing() throws Exception {
        doThrow(new UserNotFoundException(USER_ID)).when(userService).disableUser(USER_ID);

        mockMvc.perform(patch(USERS_PATH + "/" + USER_ID + "/disable")
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.adminBearerToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }
}
