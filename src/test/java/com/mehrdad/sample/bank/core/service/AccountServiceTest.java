package com.mehrdad.sample.bank.core.service;

/**
 * Created by Mehrdad Ghaderi, S&M
 * Date: 5/23/2025
 * Time: 10:32 PM
 */
//@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private AccountRepository accountRepository;
//
//    @Mock
//    private AccountMapper accountMapper;
//
//    @Mock
//    private CustomerMapper customerMapper;
//
//    @InjectMocks
//    private AccountService accountService;
//
//    @Test
//    void getAccountByAccountNumber() {
//        String accountNumber = "ACC123";
//
//        // Create mock AccountEntity and CustomerEntity
//        AccountEntity mockAccountEntity = new AccountEntity();
//        mockAccountEntity.setNumber(accountNumber);
//        CustomerEntity mockCustomerEntity = new CustomerEntity();
//        mockAccountEntity.setCustomer(mockCustomerEntity);
//
//        // Mock behavior: accountRepository.findById
//        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(mockAccountEntity));
//
//        // Mock behavior: customerMapper.toCustomerDto
//
//        // Mock behavior: accountMapper.toAccountDto
//        AccountDto mockAccountDto = new AccountDto();
//        when(accountMapper.toAccountDto(mockAccountEntity)).thenReturn(mockAccountDto);
//
//        // Act
//        AccountDto result = accountService.getAccountByAccountNumber(accountNumber);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(mockAccountDto, result);
//
//        // Verify interaction
//        verify(accountRepository, times(1)).findById(accountNumber);
//        verify(accountMapper).toAccountDto(mockAccountEntity);
//    }
//
//    @Test
//    void testGetAllAccounts_ReturnsActiveAccounts() {
//        // Arrange
//        CustomerEntity customer1 = new CustomerEntity();
//        customer1.setActive(true);
//
//        CustomerEntity customer2 = new CustomerEntity();
//        customer2.setActive(false); // should be ignored
//
//        List<CustomerEntity> customerEntities = List.of(customer1, customer2);
//
//        when(customerRepository.findAll()).thenReturn(customerEntities);
//
//        // Create DTOs for active customer
//        CustomerDto customerDto1 = new CustomerDto();
//
//        AccountDto activeAccount1 = new AccountDto();
//        activeAccount1.setActive(true);
//
//        AccountDto inactiveAccount = new AccountDto();
//        inactiveAccount.setActive(false); // should be filtered out
//
//        customerDto1.setAccounts(List.of(activeAccount1, inactiveAccount));
//
//        when(customerMapper.toCustomerDto(customer1)).thenReturn(customerDto1);
//
//        // Act
//        List<AccountDto> result = accountService.getAllAccounts();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertTrue(result.get(0).getActive());
//
//        // Verify
//        verify(customerRepository).findAll();
//        verify(customerMapper).toCustomerDto(customer1);
//        verify(customerMapper, never()).toCustomerDto(customer2); // customer2 is inactive
//    }
//
//
//    @Test
//    void save_shouldMapAndPersistAccount() {
//        // Arrange
//        AccountDto mockAccountDto = new AccountDto();
//        CustomerDto mockCustomerDto = new CustomerDto();
//
//        CustomerEntity mockCustomerEntity = new CustomerEntity();
//        AccountEntity mockAccountEntity = new AccountEntity();
//
//        // Stub customerMapper conversion (important!)
//        when(customerMapper.toCustomerEntity(mockCustomerDto)).thenReturn(mockCustomerEntity);
//
//        // Stub accountMapper conversion
//        when(accountMapper.toAccountEntity(mockAccountDto, mockCustomerEntity)).thenReturn(mockAccountEntity);
//
//        // Act
//        accountService.save(mockAccountDto, mockCustomerDto);
//
//        // Assert
//        verify(customerMapper).toCustomerEntity(mockCustomerDto);
//        verify(accountMapper).toAccountEntity(mockAccountDto, mockCustomerEntity);
//        verify(accountRepository).save(mockAccountEntity);
//    }
//
//
//
//    @Test
//    void createAccount_shouldReturnTrue_whenSaveIsSuccessful() {
//        // Arrange
//        AccountDto account = new AccountDto();
//        CustomerDto customer = new CustomerDto();
//
//        // No need to stub anything; we just want save to run without errorCode
//
//        // Act
//        boolean result = accountService.createAccount(account, customer);
//
//        // Assert
//        assertTrue(result);
//        assertTrue(account.getActive()); // check that account was set active
//        verify(accountRepository).save(any()); // check that save was called
//    }
//
//    @Test
//    void createAccount_shouldReturnFalse_whenSaveThrowsException() {
//        // Arrange
//        AccountDto account = new AccountDto();
//        CustomerDto customer = new CustomerDto();
//
//        // Stub accountRepository.save() to throw exception
//        doThrow(new RuntimeException("DB Error"))
//                .when(accountRepository).save(any());
//
//        // Act
//        boolean result = accountService.createAccount(account, customer);
//
//        // Assert
//        assertFalse(result);
//        verify(accountRepository).save(any()); // still verify save was attempted
//    }
//
//
//    @Test
//    void freezeAccount_shouldDeactivateAccount() {
//        // Arrange
//        String accountNumber = "ACC123";
//        AccountEntity mockAccount = new AccountEntity();
//        mockAccount.setNumber(accountNumber);
//        mockAccount.setActive(true);
//
//        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(mockAccount));
//
//        // Act
//        accountService.freezeAccount(accountNumber);
//
//        // Assert
//        assertFalse(mockAccount.getActive()); // account should be frozen
//        verify(accountRepository).save(mockAccount); // ensure save was called
//    }
//
//    @Test
//    void unfreezeAccount_shouldActivateAccount() {
//        // Arrange
//        String accountNumber = "ACC123";
//        AccountEntity mockAccount = new AccountEntity();
//        mockAccount.setNumber(accountNumber);
//        mockAccount.setActive(false); // assume it starts frozen
//
//        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(mockAccount));
//
//        // Act
//        accountService.unfreezeAccount(accountNumber);
//
//        // Assert
//        assertTrue(mockAccount.getActive());
//        verify(accountRepository).save(mockAccount); // ensure save was triggered
//    }

}