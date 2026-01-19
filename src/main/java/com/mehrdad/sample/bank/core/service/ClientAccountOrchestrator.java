package com.mehrdad.sample.bank.core.service;

import com.mehrdad.sample.bank.api.dto.AccountDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientAccountOrchestrator {

    private final ClientService clientService;
    private final AccountService accountService;

    public ClientAccountOrchestrator(ClientService clientService,
                                     AccountService accountService) {
        this.clientService = clientService;
        this.accountService = accountService;
    }

    @Transactional
    public void updateClientAndAccountsStatus(String clientId, boolean activate) {
        if (activate) {
            clientService.activateClient(clientId);
        } else {
            clientService.DeactivateClientById(clientId);
        }
        List<AccountDto> accounts = accountService.getAccountsByClientId(clientId);
        for (AccountDto accountDto : accounts) {
            accountService.freezeOrUnfreezeAccount(accountDto.getNumber(), activate);
        }
    }
}