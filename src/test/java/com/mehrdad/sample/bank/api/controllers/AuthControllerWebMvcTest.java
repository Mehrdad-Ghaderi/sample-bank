package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.auth.LoginRequest;
import com.mehrdad.sample.bank.security.JwtService;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SpringSecurityConfiguration.class, JwtService.class})
class AuthControllerWebMvcTest {

    private static final String LOGIN_PATH = ApiPaths.API_BASE_PATH + ApiPaths.AUTH + "/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginReturnsBearerToken() throws Exception {
        LoginRequest request = new LoginRequest("user", "pass");

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
                .andExpect(jsonPath("$.message").value("username: must not be blank"))
                .andExpect(jsonPath("$.path").value(LOGIN_PATH));
    }
}
