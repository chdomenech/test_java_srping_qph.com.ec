package com.invoicing.mapper;

import com.invoicing.dto.ProviderDTO;
import com.invoicing.entity.Provider;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    ProviderDTO toDTO(Provider provider);
    Provider toEntity(ProviderDTO dto);
}



