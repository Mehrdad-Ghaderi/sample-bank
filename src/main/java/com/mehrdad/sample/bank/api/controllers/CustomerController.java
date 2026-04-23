package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.PageResponse;
import com.mehrdad.sample.bank.api.dto.account.CreateAccountRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.customer.CreateCustomerRequest;
import com.mehrdad.sample.bank.api.dto.customer.CustomerResponse;
import com.mehrdad.sample.bank.api.dto.customer.UpdateCustomerRequest;
import com.mehrdad.sample.bank.domain.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_BASE_PATH + ApiPaths.CUSTOMERS)
public class CustomerController {

    private static final String CUSTOMER_RESOURCE_PATH = "/{customerId}";
    private static final String CUSTOMER_ACTIVATION_PATH = CUSTOMER_RESOURCE_PATH + "/activation";
    private static final String CUSTOMER_DEACTIVATION_PATH = CUSTOMER_RESOURCE_PATH + "/deactivation";
    private static final String CUSTOMER_ACCOUNTS_COLLECTION_PATH = "/{customerId}/accounts";

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<PageResponse<CustomerResponse>> getCustomers(
            @RequestParam(required = false) Integer businessId,
            @RequestParam(required = false) String phoneNumber,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(PageResponse.createFrom(customerService.getCustomers(authentication.getName(), businessId, phoneNumber, pageable)));
    }

    @GetMapping(CUSTOMER_RESOURCE_PATH)
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID customerId,
                                                       Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest createCustomerRequest,
            Authentication authentication) {
        CustomerResponse createdCustomer = customerService.createCustomer(createCustomerRequest, authentication.getName());

        URI location = buildResourceLocation(ApiPaths.CUSTOMER_RESOURCE, createdCustomer.getId());
        return ResponseEntity.created(location).body(createdCustomer);
    }

    @PatchMapping(CUSTOMER_DEACTIVATION_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateCustomer(@PathVariable UUID customerId,
                                   Authentication authentication) {
        customerService.deactivateCustomer(customerId, authentication.getName());
    }

    @PatchMapping(CUSTOMER_ACTIVATION_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateCustomer(@PathVariable UUID customerId,
                                 Authentication authentication) {
        customerService.activateCustomer(customerId, authentication.getName());
    }

    @PatchMapping(CUSTOMER_RESOURCE_PATH)
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID customerId,
                                                      @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest,
                                                      Authentication authentication) {
        CustomerResponse updatedCustomer = customerService.updateCustomer(customerId, updateCustomerRequest, authentication.getName());
        return ResponseEntity.ok(updatedCustomer);
    }

    @PostMapping(CUSTOMER_ACCOUNTS_COLLECTION_PATH)
    public ResponseEntity<AccountResponse> createCustomerAccount(@PathVariable UUID customerId,
                                                            @Valid @RequestBody CreateAccountRequest createAccountRequest,
                                                            Authentication authentication) {

        AccountResponse createdAccount = customerService.createAccount(customerId, createAccountRequest, authentication.getName());

        URI location = buildResourceLocation(ApiPaths.ACCOUNT_RESOURCE, createdAccount.getId());

        return ResponseEntity
                .created(location)
                .body(createdAccount);
    }

    @GetMapping(CUSTOMER_ACCOUNTS_COLLECTION_PATH)
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(@PathVariable UUID customerId,
                                                                Authentication authentication) {
        List<AccountResponse> accounts = customerService.getCustomerAccounts(customerId, authentication.getName());
        return ResponseEntity.ok(accounts);
    }

    private static URI buildResourceLocation(String resourcePath, Object... uriVariables) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(resourcePath)
                .buildAndExpand(uriVariables)
                .toUri();
    }
}
