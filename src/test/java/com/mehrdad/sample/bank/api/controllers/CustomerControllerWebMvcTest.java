package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.account.CreateAccountRequest;
import com.mehrdad.sample.bank.api.dto.account.AccountResponse;
import com.mehrdad.sample.bank.api.dto.customer.CreateCustomerRequest;
import com.mehrdad.sample.bank.api.dto.customer.CustomerResponse;
import com.mehrdad.sample.bank.api.dto.customer.UpdateCustomerRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
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
    private static final String AUTHENTICATED_USERNAME = "user";
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
        CustomerResponse customer = buildcustomerResponse();

        when(customerService.getCustomers(eq(AUTHENTICATED_USERNAME), eq(1001), eq(CUSTOMER_PHONE_NUMBER), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(customer), PageRequest.of(0, 5), 8));

        mockMvc.perform(get(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .param("businessId", "1001")
                        .param("phoneNumber", CUSTOMER_PHONE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(customer.getId().toString()))
                .andExpect(jsonPath("$.content[0].businessId").value(1001))
                .andExpect(jsonPath("$.content[0].name").value(customer.getName()))
                .andExpect(jsonPath("$.content[0].phoneNumber").value(customer.getPhoneNumber()))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(8))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerService).getCustomers(eq(AUTHENTICATED_USERNAME), eq(1001), eq(CUSTOMER_PHONE_NUMBER), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("createdAt")).isNotNull();
    }

    @Test
    void getCustomerByIdReturnsCustomer() throws Exception {
        CustomerResponse customer = buildcustomerResponse();

        when(customerService.getCustomerById(customer.getId(), AUTHENTICATED_USERNAME)).thenReturn(customer);

        mockMvc.perform(get(CUSTOMERS_PATH + "/" + customer.getId())
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId().toString()))
                .andExpect(jsonPath("$.businessId").value(1001))
                .andExpect(jsonPath("$.name").value(customer.getName()));

        verify(customerService).getCustomerById(customer.getId(), AUTHENTICATED_USERNAME);
    }

    @Test
    void getCustomerByIdRejectsCustomerOwnedByAnotherUser() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(customerService.getCustomerById(customerId, AUTHENTICATED_USERNAME))
                .thenThrow(new AccessDeniedException("Customer does not belong to authenticated user"));

        mockMvc.perform(get(CUSTOMERS_PATH + "/" + customerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isForbidden());

        verify(customerService).getCustomerById(customerId, AUTHENTICATED_USERNAME);
    }

    @Test
    void createCustomerReturnsCreatedWithLocationHeader() throws Exception {
        CreateCustomerRequest createCustomerRequest = buildCreateCustomerRequest();

        UUID customerId = UUID.randomUUID();
        CustomerResponse savedCustomerResponse = buildcustomerResponse();
        savedCustomerResponse.setId(customerId);
        savedCustomerResponse.setName(createCustomerRequest.getName());
        savedCustomerResponse.setPhoneNumber(createCustomerRequest.getPhoneNumber());

        when(customerService.createCustomer(any(CreateCustomerRequest.class), eq(AUTHENTICATED_USERNAME))).thenReturn(savedCustomerResponse);

        mockMvc.perform(post(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(createCustomerRequest.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(createCustomerRequest.getPhoneNumber()))
                .andExpect(header().string("Location", endsWith(CUSTOMERS_PATH + "/" + savedCustomerResponse.getId())));

        ArgumentCaptor<CreateCustomerRequest> customerCreateCaptor = ArgumentCaptor.forClass(CreateCustomerRequest.class);
        verify(customerService).createCustomer(customerCreateCaptor.capture(), eq(AUTHENTICATED_USERNAME));
        assertThat(customerCreateCaptor.getValue().getName()).isEqualTo(createCustomerRequest.getName());
        assertThat(customerCreateCaptor.getValue().getPhoneNumber()).isEqualTo(createCustomerRequest.getPhoneNumber());
    }

    @Test
    void createCustomerRequiresName() throws Exception {
        CreateCustomerRequest createCustomerRequest = buildCreateCustomerRequest();
        createCustomerRequest.setName(null);

        mockMvc.perform(post(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCustomerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.detail").value("Request validation failed."))
                .andExpect(jsonPath("$.instance").value(CUSTOMERS_PATH))
                .andExpect(jsonPath("$.violations[0].field").value("name"))
                .andExpect(jsonPath("$.violations[0].message").value("must not be blank"));

        verifyNoInteractions(customerService);
    }

    @Test
    void updateCustomerPassesRequestToService() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UpdateCustomerRequest request = new UpdateCustomerRequest("Jane Doe", CUSTOMER_PHONE_NUMBER);
        CustomerResponse response = buildcustomerResponse();
        response.setId(customerId);
        response.setName(request.getName());
        response.setPhoneNumber(request.getPhoneNumber());

        when(customerService.updateCustomer(eq(customerId), any(UpdateCustomerRequest.class), eq(AUTHENTICATED_USERNAME)))
                .thenReturn(response);

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.phoneNumber").value(CUSTOMER_PHONE_NUMBER));

        ArgumentCaptor<UpdateCustomerRequest> requestCaptor = ArgumentCaptor.forClass(UpdateCustomerRequest.class);
        verify(customerService).updateCustomer(eq(customerId), requestCaptor.capture(), eq(AUTHENTICATED_USERNAME));
        assertThat(requestCaptor.getValue().getName()).isEqualTo(request.getName());
        assertThat(requestCaptor.getValue().getPhoneNumber()).isEqualTo(request.getPhoneNumber());
    }

    @Test
    void updateCustomerRejectsCustomerOwnedByAnotherUser() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UpdateCustomerRequest request = new UpdateCustomerRequest("Jane Doe", CUSTOMER_PHONE_NUMBER);

        when(customerService.updateCustomer(eq(customerId), any(UpdateCustomerRequest.class), eq(AUTHENTICATED_USERNAME)))
                .thenThrow(new AccessDeniedException("Customer does not belong to authenticated user"));

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(customerService).updateCustomer(eq(customerId), any(UpdateCustomerRequest.class), eq(AUTHENTICATED_USERNAME));
    }

    @Test
    void updateCustomerRejectsInvalidPhoneNumber() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UpdateCustomerRequest request = new UpdateCustomerRequest("Jane Doe", INVALID_PHONE_NUMBER);

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.detail").value("Request validation failed."))
                .andExpect(jsonPath("$.instance").value(CUSTOMERS_PATH + "/" + customerId))
                .andExpect(jsonPath("$.violations[0].field").value("phoneNumber"))
                .andExpect(jsonPath("$.violations[0].message").value("Invalid phone number format"));

        verifyNoInteractions(customerService);
    }

    @Test
    void activateCustomerReturnsNoContent() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId + "/activation")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isNoContent());

        verify(customerService).activateCustomer(customerId, AUTHENTICATED_USERNAME);
    }

    @Test
    void deactivateCustomerReturnsNoContent() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId + "/deactivation")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isNoContent());

        verify(customerService).deactivateCustomer(customerId, AUTHENTICATED_USERNAME);
    }

    @Test
    void activateCustomerRejectsCustomerOwnedByAnotherUser() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        org.mockito.Mockito.doThrow(new AccessDeniedException("Customer does not belong to authenticated user"))
                .when(customerService).activateCustomer(customerId, AUTHENTICATED_USERNAME);

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId + "/activation")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(customerService).activateCustomer(customerId, AUTHENTICATED_USERNAME);
    }

    @Test
    void deactivateCustomerRejectsCustomerOwnedByAnotherUser() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        org.mockito.Mockito.doThrow(new AccessDeniedException("Customer does not belong to authenticated user"))
                .when(customerService).deactivateCustomer(customerId, AUTHENTICATED_USERNAME);

        mockMvc.perform(patch(CUSTOMERS_PATH + "/" + customerId + "/deactivation")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(customerService).deactivateCustomer(customerId, AUTHENTICATED_USERNAME);
    }

    @Test
    void createCustomerAccountReturnsCreatedWithLocationHeader() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CreateAccountRequest request = new CreateAccountRequest(Currency.CAD);
        AccountResponse response = buildAccountResponse("2026-101-000001-001");

        when(customerService.createAccount(eq(customerId), any(CreateAccountRequest.class), eq(AUTHENTICATED_USERNAME))).thenReturn(response);

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

        ArgumentCaptor<CreateAccountRequest> requestCaptor = ArgumentCaptor.forClass(CreateAccountRequest.class);
        verify(customerService).createAccount(eq(customerId), requestCaptor.capture(), eq(AUTHENTICATED_USERNAME));
        assertThat(requestCaptor.getValue().getCurrency()).isEqualTo(Currency.CAD);
    }

    @Test
    void createCustomerAccountRequiresCurrency() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        CreateAccountRequest request = new CreateAccountRequest(null);

        mockMvc.perform(post(CUSTOMERS_PATH + "/" + customerId + "/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.detail").value("Request validation failed."))
                .andExpect(jsonPath("$.instance").value(CUSTOMERS_PATH + "/" + customerId + "/accounts"))
                .andExpect(jsonPath("$.violations[0].field").value("currency"))
                .andExpect(jsonPath("$.violations[0].message").value("must not be null"));

        verifyNoInteractions(customerService);
    }

    @Test
    void getCustomerAccountsReturnsAccounts() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        AccountResponse firstAccount = buildAccountResponse("2026-101-000001-001");
        AccountResponse secondAccount = buildAccountResponse("2026-101-000001-002");

        when(customerService.getCustomerAccounts(customerId, AUTHENTICATED_USERNAME)).thenReturn(List.of(firstAccount, secondAccount));

        mockMvc.perform(get(CUSTOMERS_PATH + "/" + customerId + "/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value(firstAccount.getNumber()))
                .andExpect(jsonPath("$[1].number").value(secondAccount.getNumber()));

        verify(customerService).getCustomerAccounts(customerId, AUTHENTICATED_USERNAME);
    }

    @Test
    void getCustomerAccountsRejectsCustomerOwnedByAnotherUser() throws Exception {
        UUID customerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(customerService.getCustomerAccounts(customerId, AUTHENTICATED_USERNAME))
                .thenThrow(new AccessDeniedException("Customer does not belong to authenticated user"));

        mockMvc.perform(get(CUSTOMERS_PATH + "/" + customerId + "/accounts")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(customerService).getCustomerAccounts(customerId, AUTHENTICATED_USERNAME);
    }

    private static CreateCustomerRequest buildCreateCustomerRequest() {
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setName("John Doe");
        createCustomerRequest.setPhoneNumber(CUSTOMER_PHONE_NUMBER);
        return createCustomerRequest;
    }

    private static CustomerResponse buildcustomerResponse() {
        CustomerResponse customer = new CustomerResponse();
        customer.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        customer.setBusinessId(1001);
        customer.setName("John Doe");
        customer.setPhoneNumber(CUSTOMER_PHONE_NUMBER);
        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(Instant.parse("2026-04-19T12:00:00Z"));
        customer.setUpdatedAt(Instant.parse("2026-04-19T12:30:00Z"));
        return customer;
    }

    private static AccountResponse buildAccountResponse(String accountNumber) {
        return new AccountResponse(
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
