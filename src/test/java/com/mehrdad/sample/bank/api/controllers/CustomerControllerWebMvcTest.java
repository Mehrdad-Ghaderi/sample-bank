package com.mehrdad.sample.bank.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mehrdad.sample.bank.api.ApiPaths;
import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.domain.service.CustomerService;
import com.mehrdad.sample.bank.security.SpringSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfiguration.class)
class CustomerControllerWebMvcTest {

    private static final String CUSTOMERS_PATH = ApiPaths.API_BASE_PATH + ApiPaths.CUSTOMERS;
    private static final String BASIC_AUTH = "Basic " + Base64.getEncoder()
            .encodeToString("user:pass".getBytes(StandardCharsets.UTF_8));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void createCustomerReturnsCreatedWithLocationHeader() throws Exception {
        CustomerCreateDto customerCreateDto = new CustomerCreateDto();
        customerCreateDto.setName("John Doe");
        customerCreateDto.setPhoneNumber("5554443322");

        UUID customerId = UUID.randomUUID();
        CustomerDto savedCustomerDto = new CustomerDto.Builder()
                .id(customerId)
                .name(customerCreateDto.getName())
                .phoneNumber(customerCreateDto.getPhoneNumber())
                .build();

        when(customerService.createCustomer(any(CustomerCreateDto.class))).thenReturn(savedCustomerDto);

        mockMvc.perform(post(CUSTOMERS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH)
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

}
