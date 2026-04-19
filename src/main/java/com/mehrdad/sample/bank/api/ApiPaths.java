package com.mehrdad.sample.bank.api;

public final class ApiPaths {

    public static final String API_BASE_PATH = "/api/v1";
    public static final String CUSTOMERS = "/customers";
    public static final String ACCOUNTS = "/accounts";
    public static final String TRANSACTIONS = "/transactions";
    public static final String ACCOUNT_RESOURCE = API_BASE_PATH + ACCOUNTS + "/{accountId}";

    private ApiPaths() {
    }
}
