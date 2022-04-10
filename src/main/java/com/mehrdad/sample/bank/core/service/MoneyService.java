package com.mehrdad.sample.bank.core.service;



public class MoneyService {


    private final TransactionService transactionService;

    public MoneyService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


}
