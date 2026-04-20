package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.account.AccountCreateDto;
import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerUpdateDto;
import com.mehrdad.sample.bank.domain.entity.Currency;
import com.mehrdad.sample.bank.domain.entity.Status;
import com.mehrdad.sample.bank.domain.service.CustomerService;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfiguration.class)
class CustomerControllerWebMvcTest {

    private static final String CUSTOMERS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.CUSTOMERS;
    private static final String CUSTOMER_PHONE_NUMBER = "5554443322";
    private static final String INVALID_PHONE_NUMBER = "abc";
    private static final String BEARER_TOKEN = TestJwtTokens.bearerToken();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void getCustomersRequiresAuthentication() throws Exception {
        mockMvc.perform(get(CUSTOMERS_PATH))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(customerService);
    }

    @Test
    void getCustomersSearchesByBusinessIdAndPhoneNumber() throws Exception {
        CustomerDto customer = buildCustomerDto();

        when(customerService.getCustomers(eq(1001), eq(CUSTOMER_PHONE_NUMBER), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(customer)));

        mockMvc.perform(get(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .param("businessId", "1001")
                        .param("phoneNumber", CUSTOMER_PHONE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(customer.getId().toString()))
                .andExpect(jsonPath("$.content[0].businessId").value(1001))
                .andExpect(jsonPath("$.content[0].name").value(customer.getName()))
                .andExpect(jsonPath("$.content[0].phoneNumber").value(customer.getPhoneNumber()))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerService).getCustomers(eq(1001), eq(CUSTOMER_PHONE_NUMBER), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("createdAt")).isNotNull();
    }

    @Test
    void getCustomerByIdReturnsCustomer() throws Exception {
        CustomerDto customer = buildCustomerDto();

        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);

        mockMvc.perform(get(CUSTOMERS_PATH + "/" + customer.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId().toString()))
                .andExpect(jsonPath("$.businessId").value(1001))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(customerService).getCustomerById(customer.getId());
    }

    @Test
    void createCustomerReturnsCreatedWithLocationHeader() throws Exception {
        CustomerCreateDto customerCreateDto = buildCustomerCreateDto();

        UUID customerId = UUID.randomUUID();
        CustomerDto savedCustomerDto = buildCustomerDto();
        savedCustomerDto.setId(customerId);
        savedCustomerDto.setName(customerCreateDto.getName());
        savedCustomerDto.setPhoneNumber(customerCreateDto.getPhoneNumber());

        when(customerService.createCustomer(any(CustomerCreateDto.class))).thenReturn(savedCustomerDto);

        mockMvc.perform(post(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(customerCreateDto.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(customerCreateDto.getPhoneNumber()))
                .andExpect(header().string("Location", endsWith(CUSTOMERS_PATH + "/" + savedCustomerDto.getId())));

        ArgumentCaptor<CustomerCreateDto> customerCreateCaptor = ArgumentCaptor.forClass(CustomerCreateDto.class);
        verify(customerService).createCustomer(customerCreateCaptor.capture());
        assertThat(customerCreateCaptor.getValue().getName()).isEqualTo(customerCreateDto.getName());
        assertThat(customerCreateCaptor.getValue().getPhoneNumber()).isEqualTo(customerCreateDto.getPhoneNumber());
    }

    @Test
    void createCustomerRequiresName() throws Exception {
        CustomerCreateDto customerCreateDto = buildCustomerCreateDto();
        customerCreateDto.setName(null);

        mockMvc.perform(post(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("name: must not be blank"))
                .andExpect(jsonPath("$.path").value(CUSTOMERS_PATH));

        verifyNoInteractions(customerService);
    }

    @Test
    void updateCustomerPassesRequestToService() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CustomerUpdateDto request = new CustomerUpdateDto("Jane Doe", CUSTOMER_PHONE_NUMBER);
        CustomerDto response = buildCustomerDto();
        response.setId(customerId);
        response.setName(request.getName());
        response.setPhoneNumber(request.getPhoneNumber());

        when(customerService.updateCustomer(eq(customerId), any(CustomerUpdateDto.class))).thenReturn(response);

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(CUSTOMER_PHONE_NUMBER));

        ArgumentCaptor<CustomerUpdateDto> requestCaptor = ArgumentCaptor.forClass(CustomerUpdateDto.class);
        verify(customerService).updateCustomer(eq(customerId), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo(request.getName());
        assertThat(requestCaptor.getValue().getPhoneNumber()).isEqualTo(request.getPhoneNumber());
    }

    @Test
    void updateCustomerRejectsInvalidPhoneNumber() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CustomerUpdateDto request = new CustomerUpdateDto("Jane Doe", INVALID_PHONE_NUMBER);

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("phoneNumber: Invalid phone number format"))
                .andExpect(jsonPath("$.path").value(CUSTOMERS_PATH + "/" + customerId));

        verifyNoInteractions(customerService);
    }

    @Test
    void activateCustomerReturnsNoContent() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId + "/activation")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isNoContent());

        verify(customerService).activateCustomer(customerId);
    }

    @Test
    void deactivateCustomerReturnsNoContent() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId + "/deactivation")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isNoContent());

        verify(customerService).deactivateCustomer(customerId);
    }

    @Test
    void createCustomerAccountReturnsCreatedWithLocationHeader() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AccountCreateDto request = new AccountCreateDto(Currency.CAD);
        AccountDto response = buildAccountDto("2026-101-000001-001");

        when(customerService.createAccount(eq(customerId), any(AccountCreateDto.class))).thenReturn(response);

        mockMvc.perform(post(CUSTOMERS_PATH + "/" + customerId + "/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.number").value(response.getNumber()))
                .andExpect(jsonPath("$.currency").value("CAD"))
                .andExpect(header().string(
                        "Location",
                        endsWith(ApiPaths.API_BASE_PATH + ApiPaths.ACCOUNTS + "/" + response.getId())
                ));

        ArgumentCaptor<AccountCreateDto> requestCaptor = ArgumentCaptor.forClass(AccountCreateDto.class);
        verify(customerService).createAccount(eq(customerId), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getCurrency()).isEqualTo(Currency.CAD);
    }

    @Test
    void createCustomerAccountRequiresCurrency() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AccountCreateDto request = new AccountCreateDto(null);

        mockMvc.perform(post(CUSTOMERS_PATH + "/" + customerId + "/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("currency: must not be null"))
                .andExpect(jsonPath("$.path").value(CUSTOMERS_PATH + "/" + customerId + "/accounts"));

        verifyNoInteractions(customerService);
    }

    @Test
    void getCustomerAccountsReturnsAccounts() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AccountDto firstAccount = buildAccountDto("2026-101-000001-001");
        AccountDto secondAccount = buildAccountDto("2026-101-000001-002");

        when(customerService.getCustomerAccounts(customerId)).thenReturn(List.of(firstAccount, secondAccount));

        mockMvc.perform(get(CUSTOMERS_PATH + "/" + customerId + "/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(firstAccount.getNumber()))
                .andExpect(jsonPath("$[1].number").value(secondAccount.getNumber()));

        verify(customerService).getCustomerAccounts(customerId);
    }

    private static CustomerCreateDto buildCustomerCreateDto() {
        CustomerCreateDto customerCreateDto = new CustomerCreateDto();
        customerCreateDto.setName("John Doe");
        customerCreateDto.setPhoneNumber(CUSTOMER_PHONE_NUMBER);
        return customerCreateDto;
    }

    private static CustomerDto buildCustomerDto() {
        CustomerDto customer = new CustomerDto();
        customer.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        customer.setBusinessId(1001);
        customer.setName("John Doe");
        customer.setPhoneNumber(CUSTOMER_PHONE_NUMBER);
        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(Instant.parse("2026-04-19T12:00:00Z"));
        customer.setUpdatedAt(Instant.parse("2026-04-19T12:30:00Z"));
        return customer;
    }

    private static AccountDto buildAccountDto(String accountNumber) {
        return new AccountDto(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                accountNumber,
                Status.ACTIVE,
                Currency.CAD,
                new BigDecimal("125.75"),
                Instant.parse("2026-04-19T12:00:00Z"),
                Instant.parse("2026-04-19T12:30:00Z")
        );
    }
}
