package com.mehrdad.sample.bank.core.exception;

public class IllegalTransactionTypeException extends RuntimeException {
    public IllegalTransactionTypeException(String senderAndReceiverMustDiffer) {
        super(senderAndReceiverMustDiffer);
    }
}
