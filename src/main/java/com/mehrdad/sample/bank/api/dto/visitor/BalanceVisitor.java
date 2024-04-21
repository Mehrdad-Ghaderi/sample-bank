package com.mehrdad.sample.bank.api.dto.visitor;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.MoneyDto;
import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;
import com.mehrdad.sample.bank.core.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BalanceVisitor implements Visitor {

    AccountService accountService;

    @Override
    public List<ClientDto> visit(List<ClientDto> clientDtos) {

        return clientDtos.stream()
                .map(ClientDto::getAccounts)
                .flatMap(Collection::parallelStream)
                .map(AccountDto::getMoneys)
                .flatMap(Collection::parallelStream)
                .filter(money -> money.getAmount().compareTo(BigDecimal.TEN) >= 0)
                .map(MoneyDto::getAccount)
                .map(AccountDto::getClient)
                .collect(Collectors.toList());
    }
}
