package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.textservice.Event;
import com.mehrdad.sample.bank.api.dto.textservice.Listener;
import com.mehrdad.sample.bank.api.dto.textservice.Publisher;
import com.mehrdad.sample.bank.api.dto.visitor.Visitable;
import com.mehrdad.sample.bank.api.dto.visitor.Visitor;
import com.mehrdad.sample.bank.core.entity.AccountEntity;
import com.mehrdad.sample.bank.core.entity.ClientEntity;
import com.mehrdad.sample.bank.core.exception.ClientNotFoundException;
import com.mehrdad.sample.bank.core.mapper.ClientMapper;
import com.mehrdad.sample.bank.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ClientService implements Publisher, Visitable {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AccountService accountService;
    private List<Listener> listeners;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, AccountService accountService) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.accountService = accountService;
        listeners = new ArrayList<>();//(Arrays.asList(sender, receiver));
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

    @Override
    public void registerListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println(listener.toString() + " is subscribed to the bank's text service");
        } else {
            System.out.println("you are already subscribed");
        }
    }

    @Override
    public void removeListener(Listener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        } else {
            System.out.println(listener.toString() + " is not subscribed to the bank's text service");
        }
    }

    @Override
    public void notifyListeners(Event event) {
        for (Listener e : listeners) {
            e.onEvent(event);
        }
    }

    public List<Listener> getListeners() {
        return listeners;
    }

    @Override
    public List<ClientDto> accept(Visitor visitor) {
        var clients = getAllClients().collect(Collectors.toList());
        return visitor.visit(clients);
    }
}
