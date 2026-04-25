package com.mehrdad.sample.bank.domain.mapper;

import com.mehrdad.sample.bank.api.dto.customer.CreateCustomerRequest;
import com.mehrdad.sample.bank.api.dto.customer.CustomerResponse;
import com.mehrdad.sample.bank.domain.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Created by Mehrdad Ghaderi
 */
@Mapper(componentModel = "spring",
        uses = {AccountMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    // DTO -> Entity for creation: ignore id and businessId
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "businessId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CustomerEntity mapToCustomerEntity(CreateCustomerRequest dto);

    CustomerResponse mapToCustomerResponse(CustomerEntity entity);
}
