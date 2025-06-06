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

    public void saveClient(ClientDto client) {
        clientRepository.save(clientMapper.toClientEntity(client));
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

    public void activateClient(String clientId) {
        activateOrDeactivateClient(clientId, true);
    }

    public void deactivateClient(String clientId) {
        activateOrDeactivateClient(clientId, false);
    }

    private void activateOrDeactivateClient(String clientId, boolean status) {
        ClientEntity foundClientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        foundClientEntity.setActive(status);
        for (AccountEntity accountEntity : foundClientEntity.getAccounts()) {
            accountService.freezeOrUnfreezeAccount(accountEntity.getNumber(), status);
        }
        clientRepository.save(foundClientEntity);
    }
}
