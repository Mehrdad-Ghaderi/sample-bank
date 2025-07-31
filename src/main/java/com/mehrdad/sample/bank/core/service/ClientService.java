package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AccountService accountService;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, AccountService accountService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.accountService = accountService;
    }

    public Optional<ClientDto> getClientById(String clientId) {
        return clientRepository.findById(clientId).map(clientMapper::toClientDto);
    }

    public Stream<ClientDto> getAllClients() {
        return clientRepository.findAll().stream().map(clientMapper::toClientDto);
    }

    public ClientDto saveClient(ClientDto client) {
        ClientEntity savedClientEntity = clientRepository.save(clientMapper.toClientEntity(client));
        return clientMapper.toClientDto(savedClientEntity);
    }

    public void setClientPhoneNumber(String clientId, String phoneNumber) {
        ClientEntity foundClientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        foundClientEntity.setPhoneNumber(phoneNumber);
        clientRepository.save(foundClientEntity);
    }

    public void removeClient(ClientDto client) {
        deactivateClient(client.getId());
    }

    public void removeClientById(String clientId) {
            deactivateClient(clientId);
    }

    public void activateClient(String clientId) {
        activateOrDeactivateClient(clientId, true);
    }

    public void deactivateClient(String clientId) {
        activateOrDeactivateClient(clientId, false);
    }

    private void activateOrDeactivateClient(String clientId, boolean state) {
        ClientEntity foundClientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        foundClientEntity.setActive(state);
        for (AccountEntity accountEntity : foundClientEntity.getAccounts()) {
            accountService.freezeOrUnfreezeAccount(accountEntity.getNumber(), state);
        }
        clientRepository.save(foundClientEntity);
    }
}
