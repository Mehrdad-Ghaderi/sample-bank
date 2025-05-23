package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private ClientRepository clientRepository;     // Mocked repository for DB access
    private ClientMapper clientMapper;             // Mocked mapper to convert entity to DTO
    private AccountService accountService;         // Mocked service, unused in this test but required in constructor
    private ClientService clientService;           // System under test

    /**
     * Initializes mocks and the service before each test.
     */
    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientMapper = mock(ClientMapper.class);
        accountService = mock(AccountService.class); // Not directly used but required
        clientService = new ClientService(clientRepository, clientMapper, accountService);
    }

    /**
     * Tests that {@code getClientById} returns a ClientDto when a client is found.
     */
    @Test
    void testGetClientById_whenClientExists_returnsDto() {
        String clientId = "123";
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(clientId);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(clientId);

        // Mock behavior of repository and mapper
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toClientDto(clientEntity)).thenReturn(clientDto);

        Optional<ClientDto> result = clientService.getClientById(clientId);

        assertTrue(result.isPresent());
        assertEquals(clientId, result.get().getId());
    }

    /**
     * Tests that {@code getClientById} returns empty when client is not found.
     */
    @Test
    void testGetClientById_whenClientNotFound_returnsEmpty() {
        String clientId = "404";

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Optional<ClientDto> result = clientService.getClientById(clientId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllClients() {
    }

    @Test
    void testSaveClient() {
    }

    @Test
    void testSetClientPhoneNumber() {
    }

    @Test
    void testRemoveClient() {
    }

    @Test
    void testActivateClient() {
    }

    @Test
    void testDeactivateClient() {
    }
}