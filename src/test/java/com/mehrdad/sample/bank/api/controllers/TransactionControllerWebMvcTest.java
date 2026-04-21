package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.TransactionType;
import com.mehrdad.sample.bank.domain.service.TransactionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(SpringSecurityConfiguration.class)
class TransactionControllerWebMvcTest {

    private static final String TRANSACTIONS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.TRANSACTIONS;
    private static final String IDEMPOTENCY_KEY = "transfer-key-1";
    private static final String AUTHENTICATED_USERNAME = "user";
    private static final String BEARER_TOKEN = TestJwtTokens.bearerToken();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void getTransactionsSearchesByAccountNumber() throws Exception {
        String accountNumber = "2026-101-000046-001";
        TransactionDto transaction = buildTransactionDto();

        when(transactionService.getTransactions(eq(AUTHENTICATED_USERNAME), eq(accountNumber), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(transaction)));

        mockMvc.perform(get(TRANSACTIONS_PATH)
                        .header("Authorization", BEARER_TOKEN)
                        .param("accountNumber", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(transaction.getId().toString()))
                .andExpect(jsonPath("$.content[0].senderAccountId").value(transaction.getSenderAccountId().toString()))
                .andExpect(jsonPath("$.content[0].receiverAccountId").value(transaction.getReceiverAccountId().toString()))
                .andExpect(jsonPath("$.content[0].amount").value(25.50))
                .andExpect(jsonPath("$.content[0].currency").value("CAD"))
                .andExpect(jsonPath("$.content[0].type").value("TRANSFER"));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(transactionService).getTransactions(eq(AUTHENTICATED_USERNAME), eq(accountNumber), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("transactionTime")).isNotNull();
    }

    @Test
    void transferRequiresAuthentication() throws Exception {
        CreateTransferRequest request = buildTransferRequest();

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(transactionService);
    }

    @Test
    void transferRequiresIdempotencyKeyHeader() throws Exception {
        CreateTransferRequest request = buildTransferRequest();

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("MISSING_REQUEST_HEADER"))
                .andExpect(jsonPath("$.message").value("Missing required header: Idempotency-Key"))
                .andExpect(jsonPath("$.path").value(TRANSACTIONS_PATH + "/transfers"));

        verifyNoInteractions(transactionService);
    }

    @Test
    void transferPassesRequestAndIdempotencyKeyToService() throws Exception {
        CreateTransferRequest request = buildTransferRequest();
        TransactionDto response = buildTransferResponse(request);

        when(transactionService.transfer(any(CreateTransferRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME)))
                .thenReturn(response);

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header("Authorization", BEARER_TOKEN)
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.senderAccountId").value(request.getSenderAccountId().toString()))
                .andExpect(jsonPath("$.receiverAccountId").value(request.getReceiverAccountId().toString()))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.currency").value("CAD"))
                .andExpect(jsonPath("$.type").value("TRANSFER"));

        ArgumentCaptor<CreateTransferRequest> requestCaptor = ArgumentCaptor.forClass(CreateTransferRequest.class);
        verify(transactionService).transfer(requestCaptor.capture(), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME));

        CreateTransferRequest captured = requestCaptor.getValue();
        assertThat(captured.getSenderAccountId()).isEqualTo(request.getSenderAccountId());
        assertThat(captured.getReceiverAccountId()).isEqualTo(request.getReceiverAccountId());
        assertThat(captured.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(captured.getCurrency()).isEqualTo(request.getCurrency());
    }

    @Test
    void depositPassesRequestAndIdempotencyKeyToService() throws Exception {
        CreateDepositRequest request = buildDepositRequest();
        TransactionDto response = new TransactionDto(
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                request.getReceiverAccountId(),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.DEPOSIT,
                Instant.parse("2026-04-15T12:00:00Z")
        );

        when(transactionService.deposit(any(CreateDepositRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME)))
                .thenReturn(response);

        mockMvc.perform(post(TRANSACTIONS_PATH + "/deposits")
                        .header("Authorization", BEARER_TOKEN)
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.receiverAccountId").value(request.getReceiverAccountId().toString()));

        verify(transactionService).deposit(any(CreateDepositRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME));
    }

    @Test
    void withdrawalPassesRequestAndIdempotencyKeyToService() throws Exception {
        CreateWithdrawalRequest request = buildWithdrawalRequest();
        TransactionDto response = new TransactionDto(
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                request.getSenderAccountId(),
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.WITHDRAW,
                Instant.parse("2026-04-15T12:00:00Z")
        );

        when(transactionService.withdraw(any(CreateWithdrawalRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME)))
                .thenReturn(response);

        mockMvc.perform(post(TRANSACTIONS_PATH + "/withdrawals")
                        .header("Authorization", BEARER_TOKEN)
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("WITHDRAW"))
                .andExpect(jsonPath("$.senderAccountId").value(request.getSenderAccountId().toString()));

        verify(transactionService).withdraw(any(CreateWithdrawalRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME));
    }

    @Test
    void getTransactionsRejectsAccountOwnedByAnotherUser() throws Exception {
        String accountNumber = "2026-101-000046-001";

        when(transactionService.getTransactions(eq(AUTHENTICATED_USERNAME), eq(accountNumber), any(Pageable.class)))
                .thenThrow(new AccessDeniedException("Account does not belong to authenticated user"));

        mockMvc.perform(get(TRANSACTIONS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .param("accountNumber", accountNumber))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(transactionService).getTransactions(eq(AUTHENTICATED_USERNAME), eq(accountNumber), any(Pageable.class));
    }

    @Test
    void transferRejectsSenderAccountOwnedByAnotherUser() throws Exception {
        CreateTransferRequest request = buildTransferRequest();

        when(transactionService.transfer(any(CreateTransferRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME)))
                .thenThrow(new AccessDeniedException("Sender account does not belong to authenticated user"));

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(transactionService).transfer(any(CreateTransferRequest.class), eq(IDEMPOTENCY_KEY), eq(AUTHENTICATED_USERNAME));
    }

    private static CreateWithdrawalRequest buildWithdrawalRequest() {
        return new CreateWithdrawalRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new BigDecimal("25.50"),
                Currency.CAD
        );
    }

    private static CreateDepositRequest buildDepositRequest() {
        return new CreateDepositRequest(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("25.50"),
                Currency.CAD
        );
    }

    private static CreateTransferRequest buildTransferRequest() {
        return new CreateTransferRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("25.50"),
                Currency.CAD
        );
    }

    private static TransactionDto buildTransactionDto() {
        return new TransactionDto(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("25.50"),
                Currency.CAD,
                TransactionType.TRANSFER,
                Instant.parse("2026-04-15T12:00:00Z")
        );
    }

    private static TransactionDto buildTransferResponse(CreateTransferRequest request) {
        return new TransactionDto(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                request.getSenderAccountId(),
                request.getReceiverAccountId(),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.TRANSFER,
                Instant.parse("2026-04-15T12:00:00Z")
        );
    }
}
