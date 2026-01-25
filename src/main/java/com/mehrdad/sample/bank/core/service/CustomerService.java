package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.CustomerDto;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.CustomerAlreadyActiveException;
import com.mehrdad.sample.bank.core.exception.CustomerAlreadyExistException;
import com.mehrdad.sample.bank.core.exception.CustomerAlreadyInactiveException;
import com.mehrdad.sample.bank.core.exception.CustomerNotFoundException;
import com.mehrdad.sample.bank.core.mapper.CustomerMapper;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import com.mehrdad.sample.bank.core.util.CustomerBusinessIdGenerator;
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

    public CustomerDto getCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toCustomerDto)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public Stream<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::toCustomerDto);
    }

    public CustomerDto createCustomer(CustomerDto customer) {
        if (customerRepository.findById(customer.getId()).isPresent()) {
            throw new CustomerAlreadyExistException(customer.getId());
        }
        CustomerEntity customerEntity = customerMapper.toCustomerEntity(customer);
        customerEntity.setBusinessId(customerBusinessIdGenerator.getNextBusinessId());
        CustomerEntity savedCustomerEntity = customerRepository.save(customerEntity);
        return customerMapper.toCustomerDto(savedCustomerEntity);
    }

    public void setCustomerPhoneNumber(UUID customerId, String phoneNumber) {
        CustomerEntity foundCustomerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        foundCustomerEntity.setPhoneNumber(phoneNumber);
        customerRepository.save(foundCustomerEntity);
    }

    public void deactivateCustomer(CustomerDto customer) {
        deactivateCustomer(customer.getId());
    }

    public void DeactivateCustomerById(UUID customerId) {
        deactivateCustomer(customerId);
    }

    public void activateCustomer(UUID customerId) {
        activateOrDeactivateCustomer(customerId, Status.ACTIVE);
    }

    public void deactivateCustomer(UUID customerId) {
        activateOrDeactivateCustomer(customerId, Status.SUSPENDED);
    }

    private void activateOrDeactivateCustomer(UUID customerId, Status status) {
        CustomerEntity foundCustomerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        if (status == Status.ACTIVE && foundCustomerEntity.getStatus() == Status.ACTIVE) {
            throw new CustomerAlreadyActiveException(customerId);
        }

        if (status == Status.SUSPENDED && foundCustomerEntity.getStatus() == Status.SUSPENDED) {
            throw new CustomerAlreadyInactiveException(customerId);
        }
        foundCustomerEntity.setStatus(status);
        customerRepository.save(foundCustomerEntity);
    }
}
