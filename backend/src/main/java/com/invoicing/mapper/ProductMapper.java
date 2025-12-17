package com.invoicing.mapper;

import com.invoicing.dto.ProductDTO;
import com.invoicing.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO dto);
}



