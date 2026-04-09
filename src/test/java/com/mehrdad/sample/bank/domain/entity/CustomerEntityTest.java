package com.mehrdad.sample.bank.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CustomerEntityTest {

    @Test
    void onCreateShouldSetDefaultsWhenValuesAreMissing() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.onCreate();

        assertNotNull(customerEntity.getCreatedAt());
        assertNotNull(customerEntity.getUpdatedAt());
        assertEquals(Status.ACTIVE, customerEntity.getStatus());
        assertEquals(customerEntity.getCreatedAt(), customerEntity.getUpdatedAt());

    }

    @Test
    void onCreateShouldNotOverrideStatusWhenAlreadySet() {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setStatus(Status.SUSPENDED);

        customerEntity.onCreate();
        assertEquals(Status.SUSPENDED, customerEntity.getStatus());
        assertNotNull(customerEntity.getCreatedAt());
        assertNotNull(customerEntity.getUpdatedAt());
    }

    @Test
    void addAccountShouldMaintainBothSidesOfRelationship() {
        CustomerEntity customerEntity = new CustomerEntity();
        AccountEntity accountEntity = new AccountEntity();
        customerEntity.addAccount(accountEntity);

        AccountEntity customerAccount = customerEntity.getAccounts().stream().findFirst().orElse(null);

        assertEquals(1, customerEntity.getAccounts().size());
        assertNotNull(customerAccount);
        assertTrue(customerEntity.getAccounts().contains(accountEntity));
        assertEquals(accountEntity, customerAccount);
        assertSame(customerEntity, accountEntity.getCustomer());
    }
}
