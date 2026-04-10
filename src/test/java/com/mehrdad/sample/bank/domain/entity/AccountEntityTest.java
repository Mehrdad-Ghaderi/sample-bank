package com.mehrdad.sample.bank.domain.entity;

import com.mehrdad.sample.bank.domain.exception.transaction.InsufficientBalanceException;
import com.mehrdad.sample.bank.domain.exception.transaction.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountEntityTest {

    @Test
    void increaseBalanceShouldAddAmountToCurrentBalance() {
        AccountEntity account = new AccountEntity();
        account.setBalance(new BigDecimal("100.00"));

        account.increaseBalance(new BigDecimal("25.50"));

        assertEquals(new BigDecimal("125.50"), account.getBalance());
    }

    @Test
    void decreaseBalanceShouldSubtractAmountFromCurrentBalance() {
        AccountEntity account = new AccountEntity();
        account.setBalance(new BigDecimal("100.00"));

        account.decreaseBalance(new BigDecimal("25.50"));

        assertEquals(new BigDecimal("74.50"), account.getBalance());
    }

    @Test
    void decreaseBalanceShouldThrowWhenAmountIsGreaterThanBalance() {
        AccountEntity account = new AccountEntity();
        account.setBalance(new BigDecimal("100.00"));

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.decreaseBalance(new BigDecimal("150.00"))
        );
    }

    @Test
    void decreaseBalanceShouldThrowWhenAmountIsNegative() {
        AccountEntity account = new AccountEntity();
        account.setBalance(new BigDecimal("100.00"));

        assertThrows(
                InvalidAmountException.class,
                () -> account.decreaseBalance(new BigDecimal("-150.00"))
        );
    }

    @Test
    void increaseBalanceShouldThrowWhenAmountIsZero() {
        AccountEntity account = new AccountEntity();
        account.setBalance(new BigDecimal("100.00"));

        assertThrows(
                InvalidAmountException.class,
                () -> account.increaseBalance(BigDecimal.ZERO)
        );
    }

    @Test
    void onCreateShouldSetDefaultsWhenValuesAreMissing() {
        AccountEntity account = new AccountEntity();

        account.onCreate();

        assertEquals(Status.ACTIVE, account.getStatus());
        assertEquals(Currency.CAD, account.getCurrency());
        assertEquals(AccountRole.CUSTOMER, account.getAccountRole());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
        assertEquals(account.getCreatedAt(), account.getUpdatedAt());
    }

    @Test
    void onUpdateShouldRefreshUpdatedAtTimestamp() {
        AccountEntity account = new AccountEntity();
        Instant oldUpdatedAt = Instant.parse("2026-01-01T00:00:00Z");
        account.setUpdatedAt(oldUpdatedAt);

        account.onUpdate();

        assertNotNull(account.getUpdatedAt());
        assertTrue(account.getUpdatedAt().isAfter(oldUpdatedAt));
    }
}
