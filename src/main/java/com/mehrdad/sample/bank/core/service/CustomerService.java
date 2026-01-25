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

    public CustomerDto getCustomerById(UUID businessId) {
        return customerRepository.findById(businessId)
                .map(customerMapper::toCustomerDto)
                .orElseThrow(() -> new CustomerNotFoundException(businessId));
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

    public void DeactivateCustomerById(UUID businessId) {
        deactivateCustomer(businessId);
    }

    public void activateCustomer(UUID businessId) {
        activateOrDeactivateCustomer(businessId, Status.ACTIVE);
    }

    public void deactivateCustomer(UUID businessId) {
        activateOrDeactivateCustomer(businessId, Status.SUSPENDED);
    }

    private void activateOrDeactivateCustomer(UUID businessId, Status status) {
        CustomerEntity foundCustomerEntity = customerRepository.findById(businessId)
                .orElseThrow(() -> new CustomerNotFoundException(businessId));

        if (status == Status.ACTIVE && foundCustomerEntity.getStatus() == Status.ACTIVE) {
            throw new CustomerAlreadyActiveException(businessId);
        }

        if (status == Status.SUSPENDED && foundCustomerEntity.getStatus() == Status.SUSPENDED) {
            throw new CustomerAlreadyInactiveException(businessId);
        }
        foundCustomerEntity.setStatus(status);
        customerRepository.save(foundCustomerEntity);
    }
}
