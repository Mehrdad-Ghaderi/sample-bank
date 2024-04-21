package com.mehrdad.sample.bank.api.dto.visitor;

import com.mehrdad.sample.bank.api.dto.ClientDto;

import java.util.List;

public interface Visitable {

    List<ClientDto> accept(Visitor visitor);
}
