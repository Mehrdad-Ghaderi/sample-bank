package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.account.AccountStatusUpdateDto;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.service.AccountService;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(SpringSecurityConfiguration.class)
class AccountControllerWebMvcTest {

    private static final String ACCOUNTS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.ACCOUNTS;
    private static final String BEARER_TOKEN = TestJwtTokens.bearerToken();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void getAccountsRequiresAuthentication() throws Exception {
        mockMvc.perform(get(ACCOUNTS_PATH))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(accountService);
    }

    @Test
    void getAccountsSearchesByAccountNumber() throws Exception {
        String accountNumber = "2026-101-000046-001";
        AccountDto account = buildAccountDto(accountNumber);

        when(accountService.getAccounts(eq(accountNumber), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(account)));

        mockMvc.perform(get(ACCOUNTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .param("number", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(account.getId().toString()))
                .andExpect(jsonPath("$.content[0].number").value(accountNumber))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].currency").value("CAD"))
                .andExpect(jsonPath("$.content[0].balance").value(125.75));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(accountService).getAccounts(eq(accountNumber), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
    }

    @Test
    void getAccountByIdReturnsAccount() throws Exception {
        AccountDto account = buildAccountDto("2026-101-000046-001");

        when(accountService.getAccountById(account.getId())).thenReturn(account);

        mockMvc.perform(get(ACCOUNTS_PATH + "/" + account.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account.getId().toString()))
                .andExpect(jsonPath("$.number").value(account.getNumber()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService).getAccountById(account.getId());
    }

    @Test
    void updateAccountStatusPassesRequestToService() throws Exception {
        UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AccountStatusUpdateDto request = new AccountStatusUpdateDto(Status.SUSPENDED);
        AccountDto response = buildAccountDto("2026-101-000046-001");
        response.setId(accountId);
        response.setStatus(Status.SUSPENDED);

        when(accountService.updateAccountStatus(eq(accountId), any(AccountStatusUpdateDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch(ACCOUNTS_PATH + "/" + accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("SUSPENDED"));

        ArgumentCaptor<AccountStatusUpdateDto> requestCaptor = ArgumentCaptor.forClass(AccountStatusUpdateDto.class);
        verify(accountService).updateAccountStatus(eq(accountId), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getStatus()).isEqualTo(Status.SUSPENDED);
    }

    @Test
    void updateAccountStatusRequiresStatus() throws Exception {
        UUID accountId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AccountStatusUpdateDto request = new AccountStatusUpdateDto(null);

        mockMvc.perform(patch(ACCOUNTS_PATH + "/" + accountId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("status: must not be null"))
                .andExpect(jsonPath("$.path").value(ACCOUNTS_PATH + "/" + accountId));

        verifyNoInteractions(accountService);
    }

    private static AccountDto buildAccountDto(String accountNumber) {
        return new AccountDto(
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
