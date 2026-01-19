package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

//    @Mock
//    private ClientRepository clientRepository;
//
//    @Mock
//    private ClientMapper clientMapper;
//
//    @Mock
//    private AccountService accountService;
//
//    @InjectMocks
//    private ClientService clientService;
//
//    /**
//     * Tests that {@code getClientById} returns a ClientDto when a client is found.
//     */
//    @Test
//    void testGetClientById_whenClientExists_returnsDto() {
//        String clientId = "123";
//        ClientEntity clientEntity = new ClientEntity();
//        clientEntity.setId(clientId);
//
//        ClientDto clientDto = new ClientDto();
//        clientDto.setId(clientId);
//
//        // Mock behavior of repository and mapper
//        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
//        when(clientMapper.toClientDto(clientEntity)).thenReturn(clientDto);
//
//        ClientDto result = clientService.getClientById(clientId);
//
//        assertNotNull(result);
//        assertEquals(clientId, result.getId());
//    }
//
//    /**
//     * Tests that {@code getClientById} returns empty when client is not found.
//     */
//    @Test
//    void testGetClientById_whenClientNotFound_returnsEmpty() {
//        String clientId = "404";
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
//
//        ClientDto result = clientService.getClientById(clientId);
//
//        assertNull(result);
//    }
//
//    /**
//     * Test that {@code getAllClients()} returns a stream of correctly mapped ClientDto objects.
//     */
//    @Test
//    void testGetAllClients_returnsMappedDtos() {
//        // Arrange
//        ClientEntity client1 = new ClientEntity();
//        client1.setId("1");
//        ClientEntity client2 = new ClientEntity();
//        client2.setId("2");
//
//        ClientDto dto1 = new ClientDto();
//        dto1.setId("1");
//        ClientDto dto2 = new ClientDto();
//        dto2.setId("2");
//
//        List<ClientEntity> clientEntities = List.of(client1, client2);
//
//        when(clientRepository.findAll()).thenReturn(clientEntities);
//        when(clientMapper.toClientDto(client1)).thenReturn(dto1);
//        when(clientMapper.toClientDto(client2)).thenReturn(dto2);
//
//        // Act
//        List<ClientDto> result = clientService.getAllClients().toList();
//
//        // Assert
//        assertEquals(2, result.size(), "Should return 2 clients");
//        assertTrue(result.contains(dto1), "Should contain dto1");
//        assertTrue(result.contains(dto2), "Should contain dto2");
//
//        // Verify interaction
//        verify(clientRepository).findAll();
//        verify(clientMapper).toClientDto(client1);
//        verify(clientMapper).toClientDto(client2);
//    }
//
//    @Test
//    void testSaveClient_savesMappedClientEntity() {
//
//        ClientDto clientDto = new ClientDto();
//        clientDto.setId("123");
//
//        ClientEntity clientEntity = new ClientEntity();
//        clientEntity.setId("123");
//
//        when(clientMapper.toClientEntity(clientDto)).thenReturn(clientEntity);
//
//        // Act
//        clientService.saveClient(clientDto);
//
//        // Assert
//        verify(clientMapper).toClientEntity(clientDto);
//        verify(clientRepository).save(clientEntity);
//    }
//
//    /**
//     * Updates the phone number of a client identified by the given client ID.
//     *
//     * @throws ClientNotFoundException if no client is found with the provided ID.
//     */
//    @Test
//    void setClientPhoneNumber_shouldUpdatePhoneNumberWhenClientExists() {
//        // Arrange
//        String clientId = "123";
//        String newPhone = "555-4321";
//
//        ClientEntity clientEntity = new ClientEntity();
//        clientEntity.setId(clientId);
//        clientEntity.setPhoneNumber("000-0000");
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
//
//        // Act
//        clientService.setClientPhoneNumber(clientId, newPhone);
//
//        // Assert
//        assertEquals(newPhone, clientEntity.getPhoneNumber());
//        verify(clientRepository).save(clientEntity);
//    }
//
//    @Test
//    void setClientPhoneNumber_shouldThrowExceptionWhenClientNotFound() {
//
//        String clientId = "404";
//        String phone = "555-0000";
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
//
//        // Act + Assert
//        assertThrows(ClientNotFoundException.class, () -> clientService.setClientPhoneNumber(clientId, phone));
//
//        verify(clientRepository, never()).save(any());
//    }
//
//    @Test
//    void removeClient_shouldDeactivateClient() {
//        String clientId = "123";
//        ClientDto clientDto = new ClientDto();
//        clientDto.setId(clientId);
//
//        ClientEntity mockEntity = new ClientEntity();
//        mockEntity.setId(clientId);
//        mockEntity.setActive(true);
//        mockEntity.setAccounts(new ArrayList<>());
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mockEntity));
//
//        // Act
//        clientService.removeClient(clientDto);
//
//        // Assert
//        assertFalse(mockEntity.getActive());
//        verify(clientRepository).save(mockEntity); // ensure save is called
//    }
//
//    @Test
//    void deactivateClient_shouldDeactivateClientAndFreezeAccounts() {
//        // Given
//        String clientId = "321";
//        ClientEntity mockClientEntity = new ClientEntity();
//        mockClientEntity.setId(clientId);
//        mockClientEntity.setActive(true);
//
//        // Mock a list of accounts
//        AccountEntity account1 = new AccountEntity();
//        account1.setNumber("ACC1");
//        AccountEntity account2 = new AccountEntity();
//        account2.setNumber("ACC2");
//        mockClientEntity.setAccounts(List.of(account1, account2));
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mockClientEntity));
//
//        // When
//        clientService.deactivateClient(clientId);
//
//        // Then
//        assertFalse(mockClientEntity.getActive()); // Assert client is now deactivated
//
//        verify(accountService).freezeOrUnfreezeAccount("ACC1", false);
//        verify(accountService).freezeOrUnfreezeAccount("ACC2", false);
//        verify(clientRepository).save(mockClientEntity); // Ensure client is saved
//    }
//
//    @Test
//    void activateClient_shouldActivateClientAndUnfreezeAccounts() {
//        // Given
//        String clientId = "321";
//        ClientEntity mockClientEntity = new ClientEntity();
//        mockClientEntity.setId(clientId);
//        mockClientEntity.setActive(false);
//
//        // Mock a list of accounts
//        AccountEntity account1 = new AccountEntity();
//        account1.setNumber("ACC1");
//        AccountEntity account2 = new AccountEntity();
//        account2.setNumber("ACC2");
//        mockClientEntity.setAccounts(List.of(account1, account2));
//
//        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mockClientEntity));
//
//        // When
//        clientService.activateClient(clientId);
//
//        // Then
//        assertTrue(mockClientEntity.getActive()); // Assert client is now active
//
//        verify(accountService).freezeOrUnfreezeAccount("ACC1", true);
//        verify(accountService).freezeOrUnfreezeAccount("ACC2", true);
//        verify(clientRepository).save(mockClientEntity); // Ensure client is saved
//    }
}