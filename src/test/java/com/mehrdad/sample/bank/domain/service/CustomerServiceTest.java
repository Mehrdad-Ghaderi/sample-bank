package com.mehrdad.sample.bank.domain.service;

//@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private CustomerMapper customerMapper;
//
//    @Mock
//    private AccountService accountService;
//
//    @InjectMocks
//    private CustomerService customerService;
//
//    /**
//     * Tests that {@code getCustomerById} returns a CustomerDto when a customer is found.
//     */
//    @Test
//    void testGetCustomerById_whenCustomerExists_returnsDto() {
//        String customerId = "123";
//        CustomerEntity customerEntity = new CustomerEntity();
//        customerEntity.setId(customerId);
//
//        CustomerDto customerDto = new CustomerDto();
//        customerDto.setId(customerId);
//
//        // Mock behavior of repository and mapper
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
//        when(customerMapper.toCustomerDto(customerEntity)).thenReturn(customerDto);
//
//        CustomerDto result = customerService.getCustomerById(customerId);
//
//        assertNotNull(result);
//        assertEquals(customerId, result.getId());
//    }
//
//    /**
//     * Tests that {@code getCustomerById} returns empty when customer is not found.
//     */
//    @Test
//    void testGetCustomerById_whenCustomerNotFound_returnsEmpty() {
//        String customerId = "404";
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
//
//        CustomerDto result = customerService.getCustomerById(customerId);
//
//        assertNull(result);
//    }
//
//    /**
//     * Test that {@code getAllCustomers()} returns a stream of correctly mapped CustomerDto objects.
//     */
//    @Test
//    void testGetAllCustomers_returnsMappedDtos() {
//        // Arrange
//        CustomerEntity customer1 = new CustomerEntity();
//        customer1.setId("1");
//        CustomerEntity customer2 = new CustomerEntity();
//        customer2.setId("2");
//
//        CustomerDto dto1 = new CustomerDto();
//        dto1.setId("1");
//        CustomerDto dto2 = new CustomerDto();
//        dto2.setId("2");
//
//        List<CustomerEntity> customerEntities = List.of(customer1, customer2);
//
//        when(customerRepository.findAll()).thenReturn(customerEntities);
//        when(customerMapper.toCustomerDto(customer1)).thenReturn(dto1);
//        when(customerMapper.toCustomerDto(customer2)).thenReturn(dto2);
//
//        // Act
//        List<CustomerDto> result = customerService.getAllCustomers().toList();
//
//        // Assert
//        assertEquals(2, result.size(), "Should return 2 customers");
//        assertTrue(result.contains(dto1), "Should contain dto1");
//        assertTrue(result.contains(dto2), "Should contain dto2");
//
//        // Verify interaction
//        verify(customerRepository).findAll();
//        verify(customerMapper).toCustomerDto(customer1);
//        verify(customerMapper).toCustomerDto(customer2);
//    }
//
//    @Test
//    void testSaveCustomer_savesMappedCustomerEntity() {
//
//        CustomerDto customerDto = new CustomerDto();
//        customerDto.setId("123");
//
//        CustomerEntity customerEntity = new CustomerEntity();
//        customerEntity.setId("123");
//
//        when(customerMapper.toCustomerEntity(customerDto)).thenReturn(customerEntity);
//
//        // Act
//        customerService.saveCustomer(customerDto);
//
//        // Assert
//        verify(customerMapper).toCustomerEntity(customerDto);
//        verify(customerRepository).save(customerEntity);
//    }
//
//    /**
//     * Updates the phone number of a customer identified by the given customer ID.
//     *
//     * @throws CustomerNotFoundException if no customer is found with the provided ID.
//     */
//    @Test
//    void setCustomerPhoneNumber_shouldUpdatePhoneNumberWhenCustomerExists() {
//        // Arrange
//        String customerId = "123";
//        String newPhone = "555-4321";
//
//        CustomerEntity customerEntity = new CustomerEntity();
//        customerEntity.setId(customerId);
//        customerEntity.setPhoneNumber("000-0000");
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
//
//        // Act
//        customerService.setCustomerPhoneNumber(customerId, newPhone);
//
//        // Assert
//        assertEquals(newPhone, customerEntity.getPhoneNumber());
//        verify(customerRepository).save(customerEntity);
//    }
//
//    @Test
//    void setCustomerPhoneNumber_shouldThrowExceptionWhenCustomerNotFound() {
//
//        String customerId = "404";
//        String phone = "555-0000";
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
//
//        // Act + Assert
//        assertThrows(CustomerNotFoundException.class, () -> customerService.setCustomerPhoneNumber(customerId, phone));
//
//        verify(customerRepository, never()).save(any());
//    }
//
//    @Test
//    void removeCustomer_shouldDeactivateCustomer() {
//        String customerId = "123";
//        CustomerDto customerDto = new CustomerDto();
//        customerDto.setId(customerId);
//
//        CustomerEntity mockEntity = new CustomerEntity();
//        mockEntity.setId(customerId);
//        mockEntity.setActive(true);
//        mockEntity.setAccounts(new ArrayList<>());
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockEntity));
//
//        // Act
//        customerService.removeCustomer(customerDto);
//
//        // Assert
//        assertFalse(mockEntity.getActive());
//        verify(customerRepository).save(mockEntity); // ensure save is called
//    }
//
//    @Test
//    void deactivateCustomer_shouldDeactivateCustomerAndFreezeAccounts() {
//        // Given
//        String customerId = "321";
//        CustomerEntity mockCustomerEntity = new CustomerEntity();
//        mockCustomerEntity.setId(customerId);
//        mockCustomerEntity.setActive(true);
//
//        // Mock a list of accounts
//        AccountEntity account1 = new AccountEntity();
//        account1.setNumber("ACC1");
//        AccountEntity account2 = new AccountEntity();
//        account2.setNumber("ACC2");
//        mockCustomerEntity.setAccounts(List.of(account1, account2));
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomerEntity));
//
//        // When
//        customerService.deactivateCustomer(customerId);
//
//        // Then
//        assertFalse(mockCustomerEntity.getActive()); // Assert customer is now deactivated
//
//        verify(accountService).freezeOrUnfreezeAccount("ACC1", false);
//        verify(accountService).freezeOrUnfreezeAccount("ACC2", false);
//        verify(customerRepository).save(mockCustomerEntity); // Ensure customer is saved
//    }
//
//    @Test
//    void activateCustomer_shouldActivateCustomerAndUnfreezeAccounts() {
//        // Given
//        String customerId = "321";
//        CustomerEntity mockCustomerEntity = new CustomerEntity();
//        mockCustomerEntity.setId(customerId);
//        mockCustomerEntity.setActive(false);
//
//        // Mock a list of accounts
//        AccountEntity account1 = new AccountEntity();
//        account1.setNumber("ACC1");
//        AccountEntity account2 = new AccountEntity();
//        account2.setNumber("ACC2");
//        mockCustomerEntity.setAccounts(List.of(account1, account2));
//
//        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomerEntity));
//
//        // When
//        customerService.activateCustomer(customerId);
//
//        // Then
//        assertTrue(mockCustomerEntity.getActive()); // Assert customer is now active
//
//        verify(accountService).freezeOrUnfreezeAccount("ACC1", true);
//        verify(accountService).freezeOrUnfreezeAccount("ACC2", true);
//        verify(customerRepository).save(mockCustomerEntity); // Ensure customer is saved
//    }
}