package com.mehrdad.sample.bank.service;

import com.mehrdad.sample.bank.model.Client;
import com.mehrdad.sample.bank.repository.ClientRepository;

import java.io.*;
import java.util.*;

public class BackupService implements Serializable {

    private static final String CLIENT_PATH = "src/main/resources/backups/ClientsString.back";
    private final ClientRepository clientRepository;

    public BackupService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void backup() {
        saveAllClientsToFile();
    }

    public void restoreBackup() {
        clientRepository.setClients(loadAllClientsFromFile());
    }

    private void saveAllClientsToFile() {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CLIENT_PATH));
            for (Client client : clientRepository.getAllClients()) {
                writer.write(client.getId() + " /");
                writer.write(client.getName() + " /");
                writer.write(client.getPhoneNumber() + " /");
                writer.write(client.isNotMember() + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println("couldn't write the clientList out");
            ex.printStackTrace();
        }
    }

    private Collection<Client> loadAllClientsFromFile() {

        ArrayList<Client> clients = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CLIENT_PATH));
            String line = null;
            while ((line = reader.readLine()) != null) {
                clients.add(createClient(line));
            }
            reader.close();
            return clients;
        } catch(Exception ex) {
            throw new RuntimeException("Something went wrong during restoring clients", ex);
        }
    }

    private Client createClient(String lineToParse) {

        String[] result = lineToParse.split("/");
        boolean isMember = result[3].equals("true");
        return new Client(result[0], result[1], result[2], isMember);
    }

}
