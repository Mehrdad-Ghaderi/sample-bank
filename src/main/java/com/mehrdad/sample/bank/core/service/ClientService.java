package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientDto getClientById(String id) {
        Optional<ClientEntity> clientEntity = clientRepository.findById(id);
        return clientEntity.map(clientMapper::toClientDto).orElse(null);
    }

    public Collection<ClientDto> getAllClientDtos() {
        Iterable<ClientEntity> clients = clientRepository.findAll();
        Iterator<ClientEntity> iter = clients.iterator();
        ArrayList<ClientDto> clientDtos = new ArrayList<>();
        while (iter.hasNext()) {
            clientDtos.add(clientMapper.toClientDto(iter.next()));
        }
        return clientDtos;
    }

    public void createClientEntity(ClientDto newClient) {
        ClientEntity clientEntity = new ClientEntity(newClient.getId(), newClient.getName(), newClient.getPhoneNumber(), true);
        addClientEntity(clientEntity);
    }

    private void addClientEntity(ClientEntity clientEntity) {
        clientRepository.save(clientEntity);
    }

    public void setPhoneNumber(String id, String newPhoneNumber) {
        Optional<ClientEntity> foundClientEntity = clientRepository.findById(id);
        foundClientEntity.ifPresent(clientEntity -> clientEntity.setPhoneNumber(newPhoneNumber));
    }

    public void setActive(String id, Boolean status) {
        Optional<ClientEntity> foundClientEntity = clientRepository.findById(id);
        foundClientEntity.ifPresent(clientEntity -> clientEntity.setActive(status));

    }
}
