package com.mehrdad.sample.bank.api.controllers;

import com.mehrdad.sample.bank.api.dto.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.CustomerDto;
import com.mehrdad.sample.bank.core.service.CustomerService;
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

    public static final String CLIENTS = API_V1 + "/customers";
    public static final String CLIENT_By_ID = CLIENTS + "/{customerId}";

    private final CustomerService customerService;

    @GetMapping(CLIENTS)
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers().collect(Collectors.toList());
    }

    @GetMapping(CLIENT_By_ID)
    public CustomerDto getCustomerById(@PathVariable UUID customerId) {
        return customerService.getCustomerById(customerId);
    }

    @PostMapping(CLIENTS)
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerCreateDto customerCreateDto) {
        CustomerDto savedCustomerDto = customerService.createCustomer(customerCreateDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCustomerDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedCustomerDto);
    }

    @DeleteMapping(CLIENT_By_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomerById(@PathVariable UUID customerId) {
        customerService.deactivateCustomer(customerId);
    }

    @PostMapping(CLIENT_By_ID)
    public void activateCustomer(@PathVariable UUID customerId) {
        customerService.activateCustomer(customerId);
    }
}
