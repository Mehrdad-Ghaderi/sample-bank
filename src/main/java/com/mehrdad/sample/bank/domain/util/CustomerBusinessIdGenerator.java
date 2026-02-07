package com.mehrdad.sample.bank.domain.util;

import com.mehrdad.sample.bank.domain.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CustomerBusinessIdGenerator {

    private final CustomerRepository customerRepository;

    public CustomerBusinessIdGenerator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Integer getNextBusinessId() {
        Integer last = customerRepository.findLastBusinessId();
        return (last == null) ? 100000 : last + 1;
    }
}
