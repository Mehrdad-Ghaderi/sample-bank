package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.CreateDepositRequest;
import com.mehrdad.sample.bank.api.dto.CreateTransferRequest;
import com.mehrdad.sample.bank.api.dto.CreateWithdrawalRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.domain.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_BASE_PATH + ApiPaths.TRANSACTIONS)
public class TransactionController {
    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String TRANSFERS_PATH = "/transfers";
    private static final String DEPOSITS_PATH = "/deposits";
    private static final String WITHDRAWALS_PATH = "/withdrawals";

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @RequestParam(required = false) String accountNumber,
            @PageableDefault(size = 5, sort = "transactionTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactions(accountNumber, pageable));
    }

    @PostMapping(TRANSFERS_PATH)
    public ResponseEntity<TransactionDto> transfer(
            @RequestHeader(IDEMPOTENCY_KEY_HEADER) String idempotencyKey,
            @Valid @RequestBody CreateTransferRequest request
    ) {
        return ResponseEntity.ok(transactionService.transfer(request, idempotencyKey));
    }

    @PostMapping(DEPOSITS_PATH)
    public ResponseEntity<TransactionDto> deposit(
            @RequestHeader(IDEMPOTENCY_KEY_HEADER) String idempotencyKey,
            @Valid @RequestBody CreateDepositRequest request
    ) {
        return ResponseEntity.ok(transactionService.deposit(request, idempotencyKey));
    }

    @PostMapping(WITHDRAWALS_PATH)
    public ResponseEntity<TransactionDto> withdraw(
            @RequestHeader(IDEMPOTENCY_KEY_HEADER) String idempotencyKey,
            @Valid @RequestBody CreateWithdrawalRequest request
    ) {
        return ResponseEntity.ok(transactionService.withdraw(request, idempotencyKey));
    }
}
