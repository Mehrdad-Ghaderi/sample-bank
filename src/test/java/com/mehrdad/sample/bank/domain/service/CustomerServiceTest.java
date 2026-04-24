package com.mehrdad.sample.bank.domain.service;

import com.mehrdad.sample.bank.api.dto.account.CreateAccountRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.customer.CreateCustomerRequest;
import com.mehrdad.sample.bank.api.dto.customer.CustomerResponse;
import com.mehrdad.sample.bank.api.dto.customer.UpdateCustomerRequest;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.entity.UserEntity;
import com.mehrdad.sample.bank.domain.exception.ConcurrentUpdateException;
import com.mehrdad.sample.bank.domain.exception.customer.CustomerAlreadyActiveException;
import com.mehrdad.sample.bank.domain.exception.customer.CustomerAlreadyExistException;
import com.mehrdad.sample.bank.domain.exception.customer.CustomerAlreadyInactiveException;
import com.mehrdad.sample.bank.domain.exception.customer.CustomerNotFoundException;
import com.mehrdad.sample.bank.domain.exception.customer.PhoneNumberAlreadyExists;
import com.mehrdad.sample.bank.domain.mapper.AccountMapper;
import com.mehrdad.sample.bank.domain.mapper.CustomerMapper;
import com.mehrdad.sample.bank.domain.repository.CustomerRepository;
import com.mehrdad.sample.bank.domain.repository.UserRepository;
import com.mehrdad.sample.bank.domain.util.CustomerBusinessIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final String OWNER_USERNAME = "user";
    private static final String OTHER_USERNAME = "other-user";

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerBusinessIdGenerator customerBusinessIdGenerator;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCustomersShouldSearchWithBusinessIdAndNormalizedPhoneNumber() {
        Integer businessId = 1001;
        String rawPhoneNumber = "(416) 555-6598";
        CustomerEntity customer = customerEntity(1001);
        CustomerResponse customerResponse = customerResponse(customer.getId());
        PageRequest pageable = PageRequest.of(0, 5);

        when(customerRepository.searchCustomers(OWNER_USERNAME, businessId, "+14165556598", pageable))
                .thenReturn(new PageImpl<>(List.of(customer)));
        when(customerMapper.mapToCustomerResponse(customer)).thenReturn(customerResponse);

        var result = customerService.getCustomers(OWNER_USERNAME, businessId, rawPhoneNumber, pageable);

        assertEquals(List.of(customerResponse), result.getContent());
        verify(customerRepository).searchCustomers(OWNER_USERNAME, businessId, "+14165556598", pageable);
        verify(customerMapper).mapToCustomerResponse(customer);
    }

    @Test
    void getCustomersShouldTreatBlankPhoneNumberAsNoPhoneFilter() {
        PageRequest pageable = PageRequest.of(0, 5);

        when(customerRepository.searchCustomers(OWNER_USERNAME, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        var result = customerService.getCustomers(OWNER_USERNAME, null, "   ", pageable);

        assertTrue(result.isEmpty());
        verify(customerRepository).searchCustomers(OWNER_USERNAME, null, null, pageable);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void getCustomerByIdShouldReturnCustomerWhenOwnedByAuthenticatedUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(10);
        customer.setId(customerId);
        CustomerResponse expectedCustomer = customerResponse(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerMapper.mapToCustomerResponse(customer)).thenReturn(expectedCustomer);

        CustomerResponse result = customerService.getCustomerById(customerId, OWNER_USERNAME);

        assertEquals(expectedCustomer, result);
        verify(customerRepository).findById(customerId);
        verify(customerMapper).mapToCustomerResponse(customer);
    }

    @Test
    void getCustomerByIdShouldThrowWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void getCustomerByIdShouldThrowAccessDeniedWhenCustomerBelongsToAnotherUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(11);
        customer.setOwnerUser(user(OTHER_USERNAME));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.getCustomerById(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(customerMapper);
    }

    @Test
    void createCustomerShouldThrowWhenPhoneNumberAlreadyExists() {
        String rawPhoneNumber = "4165556598";
        String normalizedPhoneNumber = "+14165556598";

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setPhoneNumber(rawPhoneNumber);

        when(customerRepository.findByPhoneNumber(normalizedPhoneNumber))
                .thenReturn(Optional.of(customerEntity(20)));

        assertThrows(CustomerAlreadyExistException.class, () -> customerService.createCustomer(createCustomerRequest, OWNER_USERNAME));

        verify(customerRepository).findByPhoneNumber(normalizedPhoneNumber);
        verifyNoInteractions(customerMapper);
        verify(customerRepository, never()).saveAndFlush(any());
    }

    @Test
    void createCustomerShouldNormalizePhoneAssignBusinessIdAndReturnMappedCustomer() {
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setName("Alice");
        createCustomerRequest.setPhoneNumber("(416) 555-6598");

        CustomerEntity mappedCustomer = new CustomerEntity();
        CustomerEntity savedCustomer = new CustomerEntity();
        CustomerResponse expectedCustomer = customerResponse(UUID.randomUUID());

        when(customerRepository.findByPhoneNumber("+14165556598")).thenReturn(Optional.empty());
        when(customerMapper.mapToCustomerEntity(createCustomerRequest)).thenReturn(mappedCustomer);
        when(userRepository.findByUsername(OWNER_USERNAME)).thenReturn(Optional.of(user(OWNER_USERNAME)));
        when(customerBusinessIdGenerator.getNextBusinessId()).thenReturn(1001);
        when(customerRepository.saveAndFlush(mappedCustomer)).thenReturn(savedCustomer);
        when(customerMapper.mapToCustomerResponse(savedCustomer)).thenReturn(expectedCustomer);

        CustomerResponse result = customerService.createCustomer(createCustomerRequest, OWNER_USERNAME);

        assertEquals(expectedCustomer, result);
        assertEquals("+14165556598", mappedCustomer.getPhoneNumber());
        assertEquals(OWNER_USERNAME, mappedCustomer.getOwnerUser().getUsername());
        assertEquals(1001, mappedCustomer.getBusinessId());
        verify(customerRepository).findByPhoneNumber("+14165556598");
        verify(customerMapper).mapToCustomerEntity(createCustomerRequest);
        verify(userRepository).findByUsername(OWNER_USERNAME);
        verify(customerBusinessIdGenerator).getNextBusinessId();
        verify(customerRepository).saveAndFlush(mappedCustomer);
        verify(customerMapper).mapToCustomerResponse(savedCustomer);
    }

    @Test
    void activateCustomerShouldThrowWhenCustomerIsAlreadyActive() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(30);
        customer.setStatus(Status.ACTIVE);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(CustomerAlreadyActiveException.class, () -> customerService.activateCustomer(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void activateCustomerShouldSetStatusToActiveAndSaveCustomer() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(31);
        customer.setStatus(Status.SUSPENDED);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.activateCustomer(customerId, OWNER_USERNAME);

        assertEquals(Status.ACTIVE, customer.getStatus());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(customer);
    }

    @Test
    void activateCustomerShouldRejectCustomerOwnedByAnotherUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(34);
        customer.setOwnerUser(user(OTHER_USERNAME));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.activateCustomer(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deactivateCustomerShouldThrowWhenCustomerIsAlreadyInactive() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(32);
        customer.setStatus(Status.SUSPENDED);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(CustomerAlreadyInactiveException.class, () -> customerService.deactivateCustomer(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deactivateCustomerShouldSetStatusToSuspendedAndSaveCustomer() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(33);
        customer.setStatus(Status.ACTIVE);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.deactivateCustomer(customerId, OWNER_USERNAME);

        assertEquals(Status.SUSPENDED, customer.getStatus());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(customer);
    }

    @Test
    void deactivateCustomerShouldRejectCustomerOwnedByAnotherUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(35);
        customer.setOwnerUser(user(OTHER_USERNAME));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.deactivateCustomer(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomerShouldIgnoreUnchangedNameAndPhoneNumber() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity existingCustomer = customerEntity(40);
        existingCustomer.setName("Alice");
        existingCustomer.setPhoneNumber("+14165550000");

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        updateCustomerRequest.setName("Alice");
        updateCustomerRequest.setPhoneNumber("(416) 555-0000");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerMapper.mapToCustomerResponse(existingCustomer)).thenReturn(customerResponse(customerId));

        CustomerResponse result = customerService.updateCustomer(customerId, updateCustomerRequest, OWNER_USERNAME);

        assertEquals(customerId, result.getId());
        assertEquals("Alice", existingCustomer.getName());
        assertEquals("+14165550000", existingCustomer.getPhoneNumber());
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).existsByPhoneNumber(any());
        verify(customerMapper).mapToCustomerResponse(existingCustomer);
    }

    @Test
    void updateCustomerShouldThrowWhenNormalizedPhoneNumberAlreadyExists() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity existingCustomer = customerEntity(41);
        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        updateCustomerRequest.setPhoneNumber("(416) 555-9090");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByPhoneNumber("+14165559090")).thenReturn(true);

        assertThrows(PhoneNumberAlreadyExists.class,
                () -> customerService.updateCustomer(customerId, updateCustomerRequest, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByPhoneNumber("+14165559090");
        verifyNoInteractions(customerMapper);
    }

    @Test
    void updateCustomerShouldRejectCustomerOwnedByAnotherUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity existingCustomer = customerEntity(49);
        existingCustomer.setOwnerUser(user(OTHER_USERNAME));
        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest("Bob", "(647) 555-1234");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.updateCustomer(customerId, updateCustomerRequest, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).existsByPhoneNumber(any());
        verifyNoInteractions(customerMapper);
    }

    @Test
    void updateCustomerShouldApplyProvidedFieldsAndReturnMappedCustomer() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity existingCustomer = customerEntity(42);
        existingCustomer.setName("Alice");
        existingCustomer.setPhoneNumber("+14165550000");
        existingCustomer.setStatus(Status.ACTIVE);

        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        updateCustomerRequest.setName("Bob");
        updateCustomerRequest.setPhoneNumber("(647) 555-1234");

        CustomerResponse expectedCustomer = customerResponse(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByPhoneNumber("+16475551234")).thenReturn(false);
        when(customerMapper.mapToCustomerResponse(existingCustomer)).thenReturn(expectedCustomer);

        CustomerResponse result = customerService.updateCustomer(customerId, updateCustomerRequest, OWNER_USERNAME);

        assertEquals(expectedCustomer, result);
        assertEquals("Bob", existingCustomer.getName());
        assertEquals("+16475551234", existingCustomer.getPhoneNumber());
        assertEquals(Status.ACTIVE, existingCustomer.getStatus());
        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByPhoneNumber("+16475551234");
        verify(customerMapper).mapToCustomerResponse(existingCustomer);
    }

    @Test
    void updateCustomerShouldTranslateOptimisticLockingFailure() {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();

        when(customerRepository.findById(customerId))
                .thenThrow(new OptimisticLockingFailureException("conflict"));

        RuntimeException exception = assertThrows(ConcurrentUpdateException.class,
                () -> customerService.updateCustomer(customerId, updateCustomerRequest, OWNER_USERNAME));

        assertInstanceOf(ConcurrentUpdateException.class, exception);
        verify(customerRepository).findById(customerId);
    }

    @Test
    void updateCustomerShouldTranslateDataIntegrityViolation() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity existingCustomer = customerEntity(43);
        UpdateCustomerRequest updateCustomerRequest = new UpdateCustomerRequest();
        updateCustomerRequest.setPhoneNumber("6475559999");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByPhoneNumber("+16475559999"))
                .thenThrow(new DataIntegrityViolationException("duplicate phone"));

        assertThrows(PhoneNumberAlreadyExists.class,
                () -> customerService.updateCustomer(customerId, updateCustomerRequest, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository).existsByPhoneNumber("+16475559999");
    }

    @Test
    void createAccountShouldUseRequestedCurrencyAndReturnMappedAccount() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(44);
        customer.setId(customerId);
        customer.setBusinessId(44);
        customer.setAccounts(new ArrayList<>());
        AccountResponse expectedAccount = new AccountResponse();
        expectedAccount.setCurrency(Currency.CAD);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountMapper.mapToAccountResponse(any(AccountEntity.class))).thenReturn(expectedAccount);

        CreateAccountRequest createAccountRequest = new CreateAccountRequest(Currency.CAD);

        AccountResponse result = customerService.createAccount(customerId, createAccountRequest, OWNER_USERNAME);

        ArgumentCaptor<AccountEntity> accountCaptor = ArgumentCaptor.forClass(AccountEntity.class);

        assertEquals(expectedAccount, result);
        verify(customerRepository).findById(customerId);
        verify(customerRepository).saveAndFlush(customer);
        verify(accountMapper).mapToAccountResponse(accountCaptor.capture());
        assertEquals(Currency.CAD, accountCaptor.getValue().getCurrency());
        assertSame(customer, accountCaptor.getValue().getCustomer());
        assertTrue(accountCaptor.getValue().getNumber().endsWith("-001"));
    }

    @Test
    void createAccountShouldRejectCustomerOwnedByAnotherUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(47);
        customer.setId(customerId);
        customer.setOwnerUser(user(OTHER_USERNAME));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.createAccount(customerId, new CreateAccountRequest(Currency.CAD), OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).saveAndFlush(any());
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getCustomerAccountsShouldReturnEmptyListWhenCustomerHasNoAccounts() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(45);
        customer.setAccounts(List.of());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        List<AccountResponse> result = customerService.getCustomerAccounts(customerId, OWNER_USERNAME);

        assertEquals(List.of(), result);
        verify(customerRepository).findById(customerId);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getCustomerAccountsShouldReturnMappedAccounts() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(46);
        AccountEntity firstAccount = new AccountEntity();
        firstAccount.setNumber("2026-101-000046-001");
        AccountEntity secondAccount = new AccountEntity();
        secondAccount.setNumber("2026-101-000046-002");
        customer.setAccounts(List.of(firstAccount, secondAccount));
        AccountResponse firstAccountResponse = new AccountResponse();
        firstAccountResponse.setNumber(firstAccount.getNumber());
        AccountResponse secondAccountResponse = new AccountResponse();
        secondAccountResponse.setNumber(secondAccount.getNumber());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(accountMapper.mapToAccountResponse(firstAccount)).thenReturn(firstAccountResponse);
        when(accountMapper.mapToAccountResponse(secondAccount)).thenReturn(secondAccountResponse);

        List<AccountResponse> result = customerService.getCustomerAccounts(customerId, OWNER_USERNAME);

        assertEquals(List.of(firstAccountResponse, secondAccountResponse), result);
        verify(customerRepository).findById(customerId);
        verify(accountMapper).mapToAccountResponse(firstAccount);
        verify(accountMapper).mapToAccountResponse(secondAccount);
    }

    @Test
    void getCustomerAccountsShouldRejectCustomerOwnedByAnotherUser() {
        UUID customerId = UUID.randomUUID();
        CustomerEntity customer = customerEntity(48);
        customer.setOwnerUser(user(OTHER_USERNAME));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class,
                () -> customerService.getCustomerAccounts(customerId, OWNER_USERNAME));

        verify(customerRepository).findById(customerId);
        verifyNoInteractions(accountMapper);
    }

    private CustomerEntity customerEntity(int businessId) {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(UUID.randomUUID());
        customer.setBusinessId(businessId);
        customer.setName("Customer-" + businessId);
        customer.setOwnerUser(user(OWNER_USERNAME));
        customer.setPhoneNumber("+1416555" + String.format("%04d", businessId));
        customer.setStatus(Status.ACTIVE);
        customer.setAccounts(new ArrayList<>());
        return customer;
    }

    private CustomerResponse customerResponse(UUID customerId) {
        CustomerResponse customer = new CustomerResponse();
        customer.setId(customerId);
        return customer;
    }

    private UserEntity user(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        return user;
    }
}
