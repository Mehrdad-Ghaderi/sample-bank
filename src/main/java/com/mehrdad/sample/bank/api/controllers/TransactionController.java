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
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getTransaction(@PageableDefault(size = 5, sort = "transactionTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(transactionService.getTransactions(pageable));
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransactionDto> transfer(@Valid @RequestBody CreateTransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @PostMapping("/deposits")
    public ResponseEntity<TransactionDto> deposit(@Valid @RequestBody CreateDepositRequest request) {
        return ResponseEntity.ok(transactionService.deposit(request));
    }

    @PostMapping("/withdrawals")
    public ResponseEntity<TransactionDto> withdraw(@Valid @RequestBody CreateWithdrawalRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }
}
