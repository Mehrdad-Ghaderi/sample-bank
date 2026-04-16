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
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(SpringSecurityConfiguration.class)
class TransactionControllerWebMvcTest {

    private static final String TRANSACTIONS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.TRANSACTIONS;
    private static final String IDEMPOTENCY_KEY = "transfer-key-1";
    private static final String BASIC_AUTH = "Basic " + Base64.getEncoder().encodeToString("user:pass".getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void transferRequiresAuthentication() throws Exception {
        CreateTransferRequest request = transferRequest();

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(transactionService);
    }

    @Test
    void transferRequiresIdempotencyKeyHeader() throws Exception {
        CreateTransferRequest request = transferRequest();

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header("Authorization", BASIC_AUTH)
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
        CreateTransferRequest request = transferRequest();
        TransactionDto response = transactionResponse(request);

        when(transactionService.transfer(org.mockito.ArgumentMatchers.any(CreateTransferRequest.class), eq(IDEMPOTENCY_KEY)))
                .thenReturn(response);

        mockMvc.perform(post(TRANSACTIONS_PATH + "/transfers")
                        .header("Authorization", BASIC_AUTH)
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
        verify(transactionService).transfer(requestCaptor.capture(), eq(IDEMPOTENCY_KEY));

        CreateTransferRequest captured = requestCaptor.getValue();
        assertThat(captured.getSenderAccountId()).isEqualTo(request.getSenderAccountId());
        assertThat(captured.getReceiverAccountId()).isEqualTo(request.getReceiverAccountId());
        assertThat(captured.getAmount()).isEqualByComparingTo(request.getAmount());
        assertThat(captured.getCurrency()).isEqualTo(request.getCurrency());
    }

    @Test
    void depositPassesRequestAndIdempotencyKeyToService() throws Exception {
        CreateDepositRequest request = depositRequest();
        TransactionDto response = new TransactionDto(
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                request.getReceiverAccountId(),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.DEPOSIT,
                Instant.parse("2026-04-15T12:00:00Z")
        );

        when(transactionService.deposit(org.mockito.ArgumentMatchers.any(CreateDepositRequest.class), eq(IDEMPOTENCY_KEY)))
                .thenReturn(response);

        mockMvc.perform(post(TRANSACTIONS_PATH + "/deposits")
                        .header("Authorization", BASIC_AUTH)
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.receiverAccountId").value(request.getReceiverAccountId().toString()));

        verify(transactionService).deposit(org.mockito.ArgumentMatchers.any(CreateDepositRequest.class), eq(IDEMPOTENCY_KEY));
    }

    @Test
    void withdrawalPassesRequestAndIdempotencyKeyToService() throws Exception {
        CreateWithdrawalRequest request = withdrawalRequest();
        TransactionDto response = new TransactionDto(
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                request.getSenderAccountId(),
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                request.getAmount(),
                request.getCurrency(),
                TransactionType.WITHDRAW,
                Instant.parse("2026-04-15T12:00:00Z")
        );

        when(transactionService.withdraw(org.mockito.ArgumentMatchers.any(CreateWithdrawalRequest.class), eq(IDEMPOTENCY_KEY)))
                .thenReturn(response);

        mockMvc.perform(post(TRANSACTIONS_PATH + "/withdrawals")
                        .header("Authorization", BASIC_AUTH)
                        .header("Idempotency-Key", IDEMPOTENCY_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("WITHDRAW"))
                .andExpect(jsonPath("$.senderAccountId").value(request.getSenderAccountId().toString()));

        verify(transactionService).withdraw(org.mockito.ArgumentMatchers.any(CreateWithdrawalRequest.class), eq(IDEMPOTENCY_KEY));
    }

    private static CreateTransferRequest transferRequest() {
        return new CreateTransferRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("25.50"),
                Currency.CAD
        );
    }

    private static TransactionDto transactionResponse(CreateTransferRequest request) {
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

    private static CreateDepositRequest depositRequest() {
        return new CreateDepositRequest(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new BigDecimal("25.50"),
                Currency.CAD
        );
    }

    private static CreateWithdrawalRequest withdrawalRequest() {
        return new CreateWithdrawalRequest(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new BigDecimal("25.50"),
                Currency.CAD
        );
    }
}
