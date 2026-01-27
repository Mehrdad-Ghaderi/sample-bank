package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerUpdateDto;
import com.mehrdad.sample.bank.core.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Mehrdad Ghaderi
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController {

    public static final String API_V1 = "/api/v1";

    public static final String CUSTOMERS = API_V1 + "/customers";
    public static final String CUSTOMERS_ID = CUSTOMERS + "/{customerId}";

    public static final String CLIENT_ACCOUNTS = CUSTOMERS + "/{customerId}/accounts";

    private final CustomerService customerService;

    @GetMapping(CUSTOMERS)
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers().collect(Collectors.toList());
    }

    @GetMapping(CUSTOMERS_ID)
    public CustomerDto getCustomerById(@PathVariable UUID customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping(CUSTOMERS)
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCreateDto customerCreateDto) {
        CustomerDto savedCustomerDto = customerService.createCustomer(customerCreateDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCustomerDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedCustomerDto);
    }

    @DeleteMapping(CUSTOMERS_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomerById(@PathVariable UUID customerId) {
        customerService.deactivateCustomer(customerId);
    }

    @PostMapping(CUSTOMERS_ID)
    public void activateCustomer(@PathVariable UUID customerId) {
        customerService.activateCustomer(customerId);
    }

    @PatchMapping(CUSTOMERS_ID)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID customerId,
                                                      @Valid @RequestBody CustomerUpdateDto customerUpdateDto) {
        CustomerDto updated = customerService.updateCustomer(customerId, customerUpdateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * create an accounts belonging to a customer
     */
    @PostMapping(CLIENT_ACCOUNTS)
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
