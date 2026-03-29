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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_BASE_PATH + ApiPaths.CUSTOMERS)
public class CustomerController {

    public static final String CUSTOMER_ID_PATH = "/{customerId}";
    public static final String CUSTOMER_ACCOUNTS_PATH = "/{customerId}/accounts";

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerDto>> getCustomers(@PageableDefault(size = 5, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomers(pageable));
    }

    @GetMapping(CUSTOMER_ID_PATH)
    public CustomerDto getCustomerById(@PathVariable UUID customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCreateDto customerCreateDto) {
        CustomerDto savedCustomerDto = customerService.createCustomer(customerCreateDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCustomerDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedCustomerDto);
    }

    @DeleteMapping(CUSTOMER_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomerById(@PathVariable UUID customerId) {
        customerService.deactivateCustomer(customerId);
    }

    @PostMapping(CUSTOMER_ID_PATH)
    public void activateCustomer(@PathVariable UUID customerId) {
        customerService.activateCustomer(customerId);
    }

    @PatchMapping(CUSTOMER_ID_PATH)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID customerId,
                                                      @Valid @RequestBody CustomerUpdateDto customerUpdateDto) {
        CustomerDto updated = customerService.updateCustomer(customerId, customerUpdateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * create an accounts belonging to a customer
     */
    @PostMapping(CUSTOMER_ACCOUNTS_PATH)
    public ResponseEntity<AccountDto> createAccountByCustomerId(@PathVariable("customerId") UUID customerId,
                                                                @RequestBody(required = false) AccountCreateDto accountCreateDto) {

        AccountDto createdAccount = customerService.createAccount(customerId, accountCreateDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(AccountController.ACCOUNT_ID_PATH)
                .buildAndExpand(createdAccount.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdAccount);
    }

    @GetMapping(CUSTOMER_ACCOUNTS_PATH)
    public ResponseEntity<List<AccountDto>> getAccountByCustomerId(@PathVariable UUID customerId) {
        List<AccountDto> accounts = customerService.getAccountByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }
}
