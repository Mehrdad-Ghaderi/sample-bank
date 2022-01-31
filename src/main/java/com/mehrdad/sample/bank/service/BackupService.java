package com.mehrdad.sample.bank.service;

import com.mehrdad.sample.bank.model.Client;
import com.mehrdad.sample.bank.repository.ClientRepository;

import java.io.*;
import java.util.Collection;
import java.util.Collections;

public class BackupService implements Serializable {

    private static final String CLIENT_PATH = "src/main/resources/backups/Clients.back";

    private final ClientRepository clientRepository;

    public BackupService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void backup() {
        saveAllClients();
    }

    public void restoreBackup() {
        clientRepository.setClients(readAllClients());
    }

    private void saveAllClients() {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(CLIENT_PATH);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(clientRepository.getAllClients());
            objectOutputStream.close();

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong during clients back-up", e);
        }
    }

    private Collection<Client> readAllClients() {

        try {
            FileInputStream fileInputStream = new FileInputStream(CLIENT_PATH);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Collection<Client> clients = (Collection<Client>) objectInputStream.readObject();

            if ((clients == null) || clients.isEmpty()) {
                System.out.println("There is no information to restore.");
                return Collections.emptyList();
            }
            return clients;

        } catch (Exception e) {
            throw new RuntimeException("Something went wrong during restoring clients", e);
        }
    }

}
