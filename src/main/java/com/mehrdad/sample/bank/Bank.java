package com.mehrdad.sample.bank;

import com.mehrdad.sample.bank.repository.ClientRepository;
import com.mehrdad.sample.bank.service.BackupService;

public class Bank {

    private final static ClientRepository CLIENT_REPOSITORY = new ClientRepository();
    private final static BackupService BACKUP_SERVICE = new BackupService(CLIENT_REPOSITORY);

    public static BackupService getBackupService() {
        return BACKUP_SERVICE;
    }

    public static ClientRepository getClientRepository() {
        return CLIENT_REPOSITORY;
    }

}
