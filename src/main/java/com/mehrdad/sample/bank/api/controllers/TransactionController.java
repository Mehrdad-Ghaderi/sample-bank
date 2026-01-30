package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.CreateTransactionRequest;
import com.mehrdad.sample.bank.api.dto.TransactionDto;
import com.mehrdad.sample.bank.core.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TransactionController {
    public static final String API_V1 = "/api/v1";
    private static final String TRANSACTION_PATH = API_V1 + "/transactions";

    private final TransactionService transactionService;

    @GetMapping(TRANSACTION_PATH)
    public ResponseEntity<Page<TransactionDto>> getTransaction(@PageableDefault(size = 5, sort = "transactionTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactions(pageable));
    }

    @PostMapping(TRANSACTION_PATH)
    public ResponseEntity<TransactionDto> createTransactionRequest(@RequestBody CreateTransactionRequest createTransactionRequest) {
        TransactionDto transactionDto = transactionService.createTransaction(createTransactionRequest);

        return ResponseEntity.ok(transactionDto);
    }
}
