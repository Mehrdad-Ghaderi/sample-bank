package com.mehrdad.sample.bank.domain.mapper;

import com.mehrdad.sample.bank.api.dto.account.AccountDto;
import com.mehrdad.sample.bank.domain.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Created by Mehrdad Ghaderi
 */

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountDto toAccountDto(AccountEntity entity);
}
