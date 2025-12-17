package com.invoicing.mapper;

import com.invoicing.dto.CustomerDTO;
import com.invoicing.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDTO(Customer customer);
    Customer toEntity(CustomerDTO dto);
}



