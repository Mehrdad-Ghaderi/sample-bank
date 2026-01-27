package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerUpdateDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.*;
import com.mehrdad.sample.bank.core.mapper.AccountMapper;
import com.mehrdad.sample.bank.core.mapper.CustomerMapper;
import com.mehrdad.sample.bank.core.repository.CustomerRepository;
import com.mehrdad.sample.bank.core.util.AccountNumberGenerator;
import com.mehrdad.sample.bank.core.util.CustomerBusinessIdGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerBusinessIdGenerator customerBusinessIdGenerator;
    private final AccountMapper accountMapper;


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
            if (customer.getName().equals(customerUpdateDto.getName())) {
                throw new CustomerNameAlreadyExistsException(customer.getName());
            }
            customer.setName(customerUpdateDto.getName());
        }

        // update phone number if value exists
        if (customerUpdateDto.getPhoneNumber() != null) {
            String normalizedPhoneNumber = normalizePhoneNumber(customerUpdateDto.getPhoneNumber());
            System.out.println(normalizedPhoneNumber);
            // uniqueness check
            if (customerRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
                throw new PhoneNumberAlreadyExists(customerUpdateDto.getPhoneNumber());
            }
            customer.setPhoneNumber(normalizedPhoneNumber);
        }
        // update status if value exists
        if (customerUpdateDto.getStatus() != null) {
            customer.setStatus(customerUpdateDto.getStatus());
        }

        customerRepository.save(customer);

        return customerMapper.toCustomerDto(customer);
    }

    // ACCOUNT ***************************************

    @Transactional
    public AccountDto createAccount(UUID customerId) {
        CustomerEntity foundCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        AccountEntity newAccount = new AccountEntity();
        newAccount.setNumber(AccountNumberGenerator.generate(foundCustomer));
        foundCustomer.addAccount(newAccount);
        customerRepository.saveAndFlush(foundCustomer);

        return accountMapper.toAccountDto(newAccount);
    }

    private String normalizePhoneNumber(String raw) {
        String countryCode = "+1";
        if (raw == null || raw.isBlank()) {
            throw new InvalidPhoneNumberException("Phone number is required");
        }

        // remove everything except digits
        String digits = raw.replaceAll("\\D", "");


        // reject if it starts with 0
        if (digits.startsWith("0")) {
            throw new InvalidPhoneNumberException(
                    "Phone number must not start with leading zeros"
            );
        }

        // must be exactly 10 digits (US/Canada)
        if (digits.length() != 10) {
            throw new InvalidPhoneNumberException(
                    "Phone number must contain exactly 10 digits."
            );
        }

        return countryCode + digits;
    }
}
