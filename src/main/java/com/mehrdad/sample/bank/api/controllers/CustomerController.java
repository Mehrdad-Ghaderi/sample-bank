package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerUpdateDto;
import com.mehrdad.sample.bank.core.service.CustomerService;
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
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

    public static final String API_V1 = "/api/v1";

    public static final String CUSTOMERS_PATH = API_V1 + "/customers";
    public static final String CUSTOMERS_ID_PATH = CUSTOMERS_PATH + "/{customerId}";

    public static final String CUSTOMERS_ID_ACCOUNTS_PATH = CUSTOMERS_PATH + "/{customerId}/accounts";

    private final CustomerService customerService;

    @GetMapping(CUSTOMERS_PATH)
    public ResponseEntity<Page<CustomerDto>> getCustomers(@PageableDefault(size = 5, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomers(pageable));
    }

    @GetMapping(CUSTOMERS_ID_PATH)
    public CustomerDto getCustomerById(@PathVariable UUID customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping(CUSTOMERS_PATH)
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCreateDto customerCreateDto) {
        CustomerDto savedCustomerDto = customerService.createCustomer(customerCreateDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCustomerDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedCustomerDto);
    }

    @DeleteMapping(CUSTOMERS_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomerById(@PathVariable UUID customerId) {
        customerService.deactivateCustomer(customerId);
    }

    @PostMapping(CUSTOMERS_ID_PATH)
    public void activateCustomer(@PathVariable UUID customerId) {
        customerService.activateCustomer(customerId);
    }

    @PatchMapping(CUSTOMERS_ID_PATH)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID customerId,
                                                      @Valid @RequestBody CustomerUpdateDto customerUpdateDto) {
        CustomerDto updated = customerService.updateCustomer(customerId, customerUpdateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * create an accounts belonging to a customer
     */
    @PostMapping(CUSTOMERS_ID_ACCOUNTS_PATH)
    public ResponseEntity<AccountDto> createAccountByCustomerId(@PathVariable("customerId") UUID customerID) {

        AccountDto createdAccount = customerService.createAccount(customerID);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(AccountController.ACCOUNTS_ID)
                .buildAndExpand(createdAccount.getNumber())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdAccount);
    }

}
