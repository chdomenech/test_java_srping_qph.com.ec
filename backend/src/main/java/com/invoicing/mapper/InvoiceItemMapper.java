package com.invoicing.mapper;

import com.invoicing.dto.InvoiceItemDTO;
import com.invoicing.entity.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceItemMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productCode", source = "product.code")
    InvoiceItemDTO toDTO(InvoiceItem item);
    
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "product", ignore = true)
    InvoiceItem toEntity(InvoiceItemDTO dto);
}



