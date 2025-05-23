package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ClientService clientService;

   /* private ClientRepository clientRepository;     // Mocked repository for DB access
    private ClientMapper clientMapper;             // Mocked mapper to convert entity to DTO
    private AccountService accountService;         // Mocked service, unused in this test but required in constructor
    private ClientService clientService; */          // System under test


    /**
     * Initializes mocks and the service before each test.
     */
    /*@BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientMapper = mock(ClientMapper.class);
        accountService = mock(AccountService.class); // Not directly used but required
        clientService = new ClientService(clientRepository, clientMapper, accountService);
    }*/

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

    /**
     * Test that {@code getAllClients()} returns a stream of correctly mapped ClientDto objects.
     */
    @Test
    void testGetAllClients_returnsMappedDtos() {
        // Arrange
        ClientEntity client1 = new ClientEntity();
        client1.setId("1");
        ClientEntity client2 = new ClientEntity();
        client2.setId("2");

        ClientDto dto1 = new ClientDto();
        dto1.setId("1");
        ClientDto dto2 = new ClientDto();
        dto2.setId("2");

        List<ClientEntity> clientEntities = List.of(client1, client2);

        when(clientRepository.findAll()).thenReturn(clientEntities);
        when(clientMapper.toClientDto(client1)).thenReturn(dto1);
        when(clientMapper.toClientDto(client2)).thenReturn(dto2);

        // Act
        List<ClientDto> result = clientService.getAllClients().collect(Collectors.toList());

        // Assert
        assertEquals(2, result.size(), "Should return 2 clients");
        assertTrue(result.contains(dto1), "Should contain dto1");
        assertTrue(result.contains(dto2), "Should contain dto2");

        // Verify interaction
        verify(clientRepository).findAll();
        verify(clientMapper).toClientDto(client1);
        verify(clientMapper).toClientDto(client2);
    }

    @Test
    void testSaveClient_savesMappedClientEntity() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId("123");

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId("123");

        when(clientMapper.toClientEntity(clientDto)).thenReturn(clientEntity);

        // Act
        clientService.saveClient(clientDto);

        // Assert
        verify(clientMapper).toClientEntity(clientDto);
        verify(clientRepository).save(clientEntity);
    }

    /**
     * Updates the phone number of a client identified by the given client ID.
     *
     * @param clientId    The ID of the client whose phone number is to be updated.
     * @param phoneNumber The new phone number to set.
     * @throws ClientNotFoundException if no client is found with the provided ID.
     */
    @Test
    void setClientPhoneNumber_shouldUpdatePhoneNumberWhenClientExists() {
        // Arrange
        String clientId = "123";
        String newPhone = "555-4321";

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(clientId);
        clientEntity.setPhoneNumber("000-0000");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));

        // Act
        clientService.setClientPhoneNumber(clientId, newPhone);

        // Assert
        assertEquals(newPhone, clientEntity.getPhoneNumber());
        verify(clientRepository).save(clientEntity);
    }

    @Test
    void setClientPhoneNumber_shouldThrowExceptionWhenClientNotFound() {

        String clientId = "404";
        String phone = "555-0000";

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.setClientPhoneNumber(clientId, phone);
        });

        verify(clientRepository, never()).save(any());
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