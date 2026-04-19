package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerUpdateDto;
import com.mehrdad.sample.bank.domain.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<CustomerDto>> getCustomers(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomers(pageable));
    }

    @GetMapping(CUSTOMER_RESOURCE_PATH)
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(
            @Valid @RequestBody CustomerCreateDto customerCreateDto) {
        CustomerDto createdCustomer = customerService.createCustomer(customerCreateDto);

        URI location = buildResourceLocation(ApiPaths.CUSTOMER_RESOURCE, createdCustomer.getId());
        return ResponseEntity.created(location).body(createdCustomer);
    }

    @PatchMapping(CUSTOMER_DEACTIVATION_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateCustomer(@PathVariable UUID customerId) {
        customerService.deactivateCustomer(customerId);
    }

    @PatchMapping(CUSTOMER_ACTIVATION_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateCustomer(@PathVariable UUID customerId) {
        customerService.activateCustomer(customerId);
    }

    @PatchMapping(CUSTOMER_RESOURCE_PATH)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID customerId,
                                                      @Valid @RequestBody CustomerUpdateDto customerUpdateDto) {
        CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerUpdateDto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @PostMapping(CUSTOMER_ACCOUNTS_COLLECTION_PATH)
    public ResponseEntity<AccountDto> createCustomerAccount(@PathVariable UUID customerId,
                                                            @Valid @RequestBody AccountCreateDto accountCreateDto) {

        AccountDto createdAccount = customerService.createAccount(customerId, accountCreateDto);

        URI location = buildResourceLocation(ApiPaths.ACCOUNT_RESOURCE, createdAccount.getId());

        return ResponseEntity
                .created(location)
                .body(createdAccount);
    }

    @GetMapping(CUSTOMER_ACCOUNTS_COLLECTION_PATH)
    public ResponseEntity<List<AccountDto>> getAccountByCustomerId(@PathVariable UUID customerId) {
        List<AccountDto> accounts = customerService.getAccountByCustomerId(customerId);
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
