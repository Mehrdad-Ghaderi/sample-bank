package com.mehrdad.sample.bank.domain.exception.transaction;

public class IllegalTransactionTypeException extends RuntimeException {
    public IllegalTransactionTypeException(String senderAndReceiverMustDiffer) {
        super(senderAndReceiverMustDiffer);
    }
}
