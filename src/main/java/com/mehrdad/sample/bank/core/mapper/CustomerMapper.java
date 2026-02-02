package com.mehrdad.sample.bank.core.mapper;

import com.mehrdad.sample.bank.api.dto.customer.CustomerCreateDto;
import com.mehrdad.sample.bank.api.dto.customer.CustomerDto;
import com.mehrdad.sample.bank.core.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

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
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CustomerEntity toCustomerEntity(CustomerCreateDto dto);


    CustomerDto toCustomerDto(CustomerEntity entity);


    List<CustomerDto> toCustomerDtoList(List<CustomerEntity> entities);

    List<CustomerEntity> toCustomerEntityList(List<CustomerDto> dtos);

}