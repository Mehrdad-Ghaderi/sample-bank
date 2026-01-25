package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.CustomerDto;
import com.mehrdad.sample.bank.api.dto.CustomerUpdateDto;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.*;
import com.mehrdad.sample.bank.core.mapper.CustomerMapper;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import com.mehrdad.sample.bank.core.util.CustomerBusinessIdGenerator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerBusinessIdGenerator customerBusinessIdGenerator;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, CustomerBusinessIdGenerator customerBusinessIdGenerator) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerBusinessIdGenerator = customerBusinessIdGenerator;
    }

    public CustomerDto getCustomerById(UUID businessId) {
        return customerRepository.findById(businessId)
                .map(customerMapper::toCustomerDto)
                .orElseThrow(() -> new CustomerNotFoundException(businessId));
    }

    public Stream<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::toCustomerDto);
    }

    public CustomerDto createCustomer(CustomerCreateDto customerCreateDto) {
        if (customerRepository.findByPhoneNumber(customerCreateDto.getPhoneNumber()).isPresent()) {
            throw new CustomerAlreadyExistException(customerCreateDto.getPhoneNumber());
        }
        CustomerEntity customerEntity = customerMapper.toCustomerEntity(customerCreateDto);
        customerEntity.setBusinessId(customerBusinessIdGenerator.getNextBusinessId());
        CustomerEntity savedCustomerEntity = customerRepository.save(customerEntity);
        return customerMapper.toCustomerDto(savedCustomerEntity);
    }

    public void activateCustomer(UUID id) {
        activateOrDeactivateCustomer(id, Status.ACTIVE);
    }

    public void deactivateCustomer(UUID id) {
        activateOrDeactivateCustomer(id, Status.SUSPENDED);
    }

    private void activateOrDeactivateCustomer(UUID id, Status status) {
        CustomerEntity foundCustomerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (status == Status.ACTIVE && foundCustomerEntity.getStatus() == Status.ACTIVE) {
            throw new CustomerAlreadyActiveException(id);
        }

        if (status == Status.SUSPENDED && foundCustomerEntity.getStatus() == Status.SUSPENDED) {
            throw new CustomerAlreadyInactiveException(id);
        }
        foundCustomerEntity.setStatus(status);
        customerRepository.save(foundCustomerEntity);
    }

    public CustomerDto updateCustomer(UUID customerId, @Valid CustomerUpdateDto customerUpdateDto) {

        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // update update if value exists
        if (customerUpdateDto.getName() != null) {
            customer.setName(customerUpdateDto.getName());
        }
        // update phone number if value exists
        if (customerUpdateDto.getPhoneNumber() != null) {
            // uniqueness check
            if (customerRepository.existsByPhoneNumber(customerUpdateDto.getPhoneNumber())) {
                throw new PhoneNumberAlreadyExists(customerUpdateDto.getPhoneNumber());
            }
            customer.setPhoneNumber(customerUpdateDto.getPhoneNumber());
        }
        // update status if value exists
        if (customerUpdateDto.getStatus() != null) {
            customer.setStatus(customerUpdateDto.getStatus());
        }

        return customerMapper.toCustomerDto(customer);
    }
}
