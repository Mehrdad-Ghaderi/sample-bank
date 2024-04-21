package com.mehrdad.sample.bank.api.dto.visitor;

import com.mehrdad.sample.bank.api.dto.ClientDto;
import com.mehrdad.sample.bank.api.dto.accountdecorator.AccountDto;

import java.util.List;

public interface Visitor {

    List<ClientDto> visit(List<ClientDto> clientDto);
}
