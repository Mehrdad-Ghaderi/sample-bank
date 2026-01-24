package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.CustomerDto;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import org.springframework.stereotype.Component;

/**
 * Created by Mehrdad Ghaderi
 */
@Component
public class CustomerMapper {

    private final AccountMapper accountMapper;

    public CustomerMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public CustomerDto toCustomerDto(CustomerEntity customerEntity) {
        if (customerEntity == null) {
            return null;
        }
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customerEntity.getId());
        customerDto.setName(customerEntity.getName());
        customerDto.setBusinessId(customerEntity.getBusinessId());
        customerDto.setPhoneNumber(customerEntity.getPhoneNumber());
        customerDto.setStatus(customerEntity.getStatus());
        customerDto.setAccounts(accountMapper.toAccountDtoList(customerEntity.getAccounts()));

        return customerDto;
    }

    public CustomerEntity toCustomerEntity(CustomerDto customerDto) {
        if (customerDto == null) {
            return null;
        }
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerDto.getId());
        customerEntity.setName(customerDto.getName());
        customerEntity.setBusinessId(customerEntity.getBusinessId());
        customerEntity.setPhoneNumber(customerDto.getPhoneNumber());
        customerEntity.setStatus(customerDto.getStatus());
        customerEntity.setAccounts(accountMapper.toAccountEntityList(customerDto.getAccounts(), customerEntity));

        if (customerEntity.getAccounts() != null) {
            customerEntity.getAccounts().forEach(account -> account.setCustomer(customerEntity));
        }

        return customerEntity;
    }
}
