package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.entity.Status;
import com.mehrdad.sample.bank.core.exception.ClientAlreadyActiveException;
import com.mehrdad.sample.bank.core.exception.ClientAlreadyExistException;
import com.mehrdad.sample.bank.core.exception.ClientAlreadyInactiveException;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Created by Mehrdad Ghaderi
 */
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientDto getClientById(String clientId) {
        return clientRepository.findById(clientId)
                .map(clientMapper::toClientDto)
                .orElseThrow(() ->new ClientNotFoundException(clientId));
    }

    public Stream<ClientDto> getAllClients() {
        return clientRepository.findAll().stream().map(clientMapper::toClientDto);
    }

    public ClientDto createClient(ClientDto client) {
        if (clientRepository.findById(client.getId()).isPresent()) {
            throw new ClientAlreadyExistException(client.getId());
        }
        ClientEntity savedClientEntity = clientRepository.save(clientMapper.toClientEntity(client));
        return clientMapper.toClientDto(savedClientEntity);
    }

    public void setClientPhoneNumber(String clientId, String phoneNumber) {
        ClientEntity foundClientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        foundClientEntity.setPhoneNumber(phoneNumber);
        clientRepository.save(foundClientEntity);
    }

    public void deactivateClient(ClientDto client) {
        deactivateClient(client.getId());
    }

    public void DeactivateClientById(String clientId) {
            deactivateClient(clientId);
    }

    public void activateClient(String clientId) {
        activateOrDeactivateClient(clientId, Status.ACTIVE);
    }

    public void deactivateClient(String clientId) {
        activateOrDeactivateClient(clientId, Status.INACTIVE);
    }

    private void activateOrDeactivateClient(String clientId, Status status) {
        ClientEntity foundClientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (status == Status.ACTIVE && foundClientEntity.getStatus() == Status.ACTIVE) {
            throw new ClientAlreadyActiveException(clientId);
        }

        if (status == Status.INACTIVE && foundClientEntity.getStatus() == Status.INACTIVE) {
            throw new ClientAlreadyInactiveException(clientId);
        }
        foundClientEntity.setStatus(status);
        clientRepository.save(foundClientEntity);
    }
}
