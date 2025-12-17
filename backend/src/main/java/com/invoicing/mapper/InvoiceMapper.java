package com.invoicing.mapper;

import com.invoicing.dto.InvoiceDTO;
import com.invoicing.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InvoiceItemMapper.class})
public interface InvoiceMapper {
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "providerId", source = "provider.id")
    @Mapping(target = "providerName", source = "provider.name")
    InvoiceDTO toDTO(Invoice invoice);
    
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "items", ignore = true)
    Invoice toEntity(InvoiceDTO dto);
}



