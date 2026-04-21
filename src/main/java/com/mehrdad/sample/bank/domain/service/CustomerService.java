package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.account.CreateAccountRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.customer.CreateCustomerRequest;
import com.mehrdad.sample.bank.api.dto.customer.CustomerResponse;
import com.mehrdad.sample.bank.api.dto.customer.UpdateCustomerRequest;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.exception.ConcurrentUpdateException;
import com.mehrdad.sample.bank.domain.exception.customer.*;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.mapper.CustomerMapper;
import com.mehrdad.sample.bank.domain.repository.CustomerRepository;
import com.mehrdad.sample.bank.domain.util.AccountNumberGenerator;
import com.mehrdad.sample.bank.domain.util.CustomerBusinessIdGenerator;
import com.mehrdad.sample.bank.domain.util.PhoneNumberNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerBusinessIdGenerator customerBusinessIdGenerator;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getCustomers(String ownerUsername, Integer businessId, String phoneNumber, Pageable pageable) {
        String normalizedPhoneNumber = normalizeOptionalPhoneNumber(phoneNumber);

        return customerRepository.searchCustomers(ownerUsername, businessId, normalizedPhoneNumber, pageable)
                .map(customerMapper::mapToCustomerResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID customerId, String ownerUsername) {
        CustomerEntity customer = loadCustomerById(customerId);
        validateCustomerOwnership(customer, ownerUsername);
        return customerMapper.mapToCustomerResponse(customer);
    }

    public CustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest, String ownerUsername) {

        String normalizedPhoneNumber = PhoneNumberNormalizer.normalizePhoneNumber(
                createCustomerRequest.getPhoneNumber());

        if (customerRepository.findByPhoneNumber(normalizedPhoneNumber).isPresent()) {
            throw new CustomerAlreadyExistException(normalizedPhoneNumber);
        }

        CustomerEntity customerEntity = customerMapper.mapToCustomerEntity(createCustomerRequest);
        customerEntity.setPhoneNumber(normalizedPhoneNumber);
        customerEntity.setOwnerUsername(ownerUsername);
        customerEntity.setBusinessId(customerBusinessIdGenerator.getNextBusinessId());
        CustomerEntity savedCustomerEntity = customerRepository.saveAndFlush(customerEntity);
        return customerMapper.mapToCustomerResponse(savedCustomerEntity);
    }

    private String normalizeOptionalPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return null;
        }
        return PhoneNumberNormalizer.normalizePhoneNumber(phoneNumber);
    }

    public void activateCustomer(UUID id) {
        activateOrDeactivateCustomer(id, Status.ACTIVE, null);
    }

    public void activateCustomer(UUID id, String ownerUsername) {
        activateOrDeactivateCustomer(id, Status.ACTIVE, ownerUsername);
    }

    public void deactivateCustomer(UUID id) {
        activateOrDeactivateCustomer(id, Status.SUSPENDED, null);
    }

    public void deactivateCustomer(UUID id, String ownerUsername) {
        activateOrDeactivateCustomer(id, Status.SUSPENDED, ownerUsername);
    }

    private void activateOrDeactivateCustomer(UUID id, Status status, String ownerUsername) {
        CustomerEntity foundCustomerEntity = loadCustomerById(id);
        validateCustomerOwnershipIfPresent(foundCustomerEntity, ownerUsername);

        if (status == Status.ACTIVE && foundCustomerEntity.getStatus() == Status.ACTIVE) {
            throw new CustomerAlreadyActiveException(id);
        }

        if (status == Status.SUSPENDED && foundCustomerEntity.getStatus() == Status.SUSPENDED) {
            throw new CustomerAlreadyInactiveException(id);
        }
        foundCustomerEntity.setStatus(status);
        customerRepository.save(foundCustomerEntity);
    }

    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest updateCustomerRequest) {
        return updateCustomer(customerId, updateCustomerRequest, null);
    }

    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest updateCustomerRequest, String ownerUsername) {
        try {
            CustomerEntity foundCustomer = loadCustomerById(customerId);
            validateCustomerOwnershipIfPresent(foundCustomer, ownerUsername);

            if (updateCustomerRequest.getName() != null
                    && !updateCustomerRequest.getName().equals(foundCustomer.getName())) {
                foundCustomer.setName(updateCustomerRequest.getName());
            }

            if (updateCustomerRequest.getPhoneNumber() != null) {
                String normalizedPhoneNumber = PhoneNumberNormalizer.normalizePhoneNumber(updateCustomerRequest.getPhoneNumber());

                if (!normalizedPhoneNumber.equals(foundCustomer.getPhoneNumber())) {
                    if (customerRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
                        throw new PhoneNumberAlreadyExists(updateCustomerRequest.getPhoneNumber());
                    }
                    foundCustomer.setPhoneNumber(normalizedPhoneNumber);
                }
            }

            return customerMapper.mapToCustomerResponse(foundCustomer);
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentUpdateException("Customer was updated concurrently. Please retry.", ex);
        } catch (DataIntegrityViolationException ex) {
            throw new PhoneNumberAlreadyExists("Phone number already exists");
        }
    }

    // ACCOUNT ***************************************

    @Transactional
    public AccountResponse createAccount(UUID customerId, CreateAccountRequest createAccountRequest, String ownerUsername) {
        CustomerEntity foundCustomer = loadCustomerById(customerId);
        validateCustomerOwnership(foundCustomer, ownerUsername);

        AccountEntity newAccount = new AccountEntity();
        newAccount.setNumber(AccountNumberGenerator.generate(foundCustomer));
        newAccount.setCurrency(createAccountRequest.getCurrency());

        foundCustomer.addAccount(newAccount);
        customerRepository.saveAndFlush(foundCustomer);

        AccountEntity managedAccount = foundCustomer.getAccounts()
                .stream()
                .filter(a -> a.getNumber().equals(newAccount.getNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Created account was not attached to customer"));

        return accountMapper.mapToAccountResponse(managedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getCustomerAccounts(UUID customerId, String ownerUsername) {
        CustomerEntity foundCustomer = loadCustomerById(customerId);
        validateCustomerOwnership(foundCustomer, ownerUsername);

        return foundCustomer.getAccounts().stream()
                .map(accountMapper::mapToAccountResponse)
                .toList();
    }

    private CustomerEntity loadCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    private void validateCustomerOwnership(CustomerEntity customer, String ownerUsername) {
        if (!ownerUsername.equals(customer.getOwnerUsername())) {
            throw new AccessDeniedException("Customer does not belong to authenticated user");
        }
    }

    private void validateCustomerOwnershipIfPresent(CustomerEntity customer, String ownerUsername) {
        if (ownerUsername != null) {
            validateCustomerOwnership(customer, ownerUsername);
        }
    }
}
