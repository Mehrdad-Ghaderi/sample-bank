package com.mehrdad.sample.bank.core.exception.transaction;

public class IllegalTransactionTypeException extends RuntimeException {
    public IllegalTransactionTypeException(String senderAndReceiverMustDiffer) {
        super(senderAndReceiverMustDiffer);
    }
}
