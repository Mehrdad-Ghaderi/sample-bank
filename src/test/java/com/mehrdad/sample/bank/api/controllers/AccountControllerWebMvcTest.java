package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.account.UpdateAccountStatusRequest;
import com.mehrdad.sample.bank.api.error.ProblemDetailsFactory;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import com.mehrdad.sample.bank.domain.service.AccountService;
import com.mehrdad.sample.bank.security.DatabaseUserDetailsService;
import com.mehrdad.sample.bank.security.ProblemDetailsSecurityHandler;
import com.mehrdad.sample.bank.security.RevokedAccessTokenService;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import({
        SpringSecurityConfiguration.class,
        DatabaseUserDetailsService.class,
        ProblemDetailsFactory.class,
        ProblemDetailsSecurityHandler.class
})
class AccountControllerWebMvcTest {

    private static final String ACCOUNTS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.ACCOUNTS;
    private static final String AUTHENTICATED_USERNAME = "user";
    private static final String BEARER_TOKEN = TestJwtTokens.bearerToken();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RevokedAccessTokenService revokedAccessTokenService;

    @Test
    void getAccountsRequiresAuthentication() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/authentication-required"))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("A valid bearer token is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH))
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsRejectsMalformedBearerToken() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/authentication-required"))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("A valid bearer token is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH))
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsRejectsExpiredBearerToken() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.expiredBearerToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/authentication-required"))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("A valid bearer token is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH))
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsRejectsBearerTokenSignedWithWrongSecret() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerTokenSignedWithWrongSecret()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/authentication-required"))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("A valid bearer token is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH))
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsRejectsBearerTokenWithWrongIssuer() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerTokenWithWrongIssuer()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/authentication-required"))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("A valid bearer token is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH))
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsRejectsBearerTokenWithWrongAudience() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerTokenWithWrongAudience()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/authentication-required"))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("A valid bearer token is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH))
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsRejectsRevokedBearerToken() throws Exception {
        when(revokedAccessTokenService.isRevoked(anyString())).thenReturn(true);

        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, TestJwtTokens.bearerToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_REQUIRED"));

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsSearchesByAccountNumber() throws Exception {
        String accountNumber = "2026-101-000046-001";
        AccountResponse account = buildAccountResponse(accountNumber);

        when(accountService.getAccounts(eq(AUTHENTICATED_USERNAME), eq(accountNumber), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(account), PageRequest.of(0, 5), 12));

        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .param("number", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(account.getId().toString()))
                .andExpect(jsonPath("$.content[0].number").value(accountNumber))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].currency").value("CAD"))
                .andExpect(jsonPath("$.content[0].balance").value(125.75))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(accountService).getAccounts(eq(AUTHENTICATED_USERNAME), eq(accountNumber), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
    }

    @Test
    void getAccountByIdReturnsAccount() throws Exception {
        AccountResponse account = buildAccountResponse("2026-101-000046-001");

        when(accountService.getAccountById(account.getId(), AUTHENTICATED_USERNAME)).thenReturn(account);

        mockMvc.perform(get(ACCOUNTS_PATH + "/" + account.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.number").value(account.getNumber()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService).getAccountById(account.getId(), AUTHENTICATED_USERNAME);
    }

    @Test
    void getAccountByIdRejectsAccountOwnedByAnotherUser() throws Exception {
        UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(accountService.getAccountById(accountId, AUTHENTICATED_USERNAME))
                .thenThrow(new AccessDeniedException("Account does not belong to authenticated user"));

        mockMvc.perform(get(ACCOUNTS_PATH + "/" + accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/access-denied"))
                .andExpect(jsonPath("$.title").value("Access denied"))
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(accountService).getAccountById(accountId, AUTHENTICATED_USERNAME);
    }

    @Test
    void getAccountByIdReturnsProblemDetailForUnexpectedErrors() throws Exception {
        UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(accountService.getAccountById(accountId, AUTHENTICATED_USERNAME))
                .thenThrow(new IllegalStateException("Database connection failed"));

        mockMvc.perform(get(ACCOUNTS_PATH + "/" + accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/internal-server-error"))
                .andExpect(jsonPath("$.title").value("Internal server error"))
                .andExpect(jsonPath("$.detail").value("An unexpected error occurred."))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));

        verify(accountService).getAccountById(accountId, AUTHENTICATED_USERNAME);
    }

    @Test
    void updateAccountStatusPassesRequestToService() throws Exception {
        UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(Status.SUSPENDED);
        AccountResponse response = buildAccountResponse("2026-101-000046-001");
        response.setId(accountId);
        response.setStatus(Status.SUSPENDED);

        when(accountService.updateAccountStatus(eq(accountId), any(UpdateAccountStatusRequest.class), eq(AUTHENTICATED_USERNAME)))
                .thenReturn(response);

        mockMvc.perform(patch(ACCOUNTS_PATH + "/" + accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("SUSPENDED"));

        ArgumentCaptor<UpdateAccountStatusRequest> requestCaptor = ArgumentCaptor.forClass(UpdateAccountStatusRequest.class);
        verify(accountService).updateAccountStatus(eq(accountId), requestCaptor.capture(), eq(AUTHENTICATED_USERNAME));
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(Status.SUSPENDED);
    }

    @Test
    void updateAccountStatusRequiresStatus() throws Exception {
        UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UpdateAccountStatusRequest request = new UpdateAccountStatusRequest(null);

        mockMvc.perform(patch(ACCOUNTS_PATH + "/" + accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("https://api.sample-bank.local/problems/validation-failed"))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.detail").value("Request validation failed."))
                .andExpect(jsonPath("$.instance").value(ACCOUNTS_PATH + "/" + accountId))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.violations[0].field").value("status"))
                .andExpect(jsonPath("$.violations[0].message").value("must not be null"));

        verifyNoInteractions(accountService);
    }

    private static AccountResponse buildAccountResponse(String accountNumber) {
        return new AccountResponse(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                accountNumber,
                Status.ACTIVE,
                Currency.CAD,
                new BigDecimal("125.75"),
                Instant.parse("2026-04-19T12:00:00Z"),
                Instant.parse("2026-04-19T12:30:00Z")
        );
    }
}
