package com.invoicing.service;

import com.invoicing.dto.PageResponse;
import com.invoicing.dto.ProviderDTO;
import com.invoicing.entity.Provider;
import com.invoicing.mapper.ProviderMapper;
import com.invoicing.repository.ProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProviderService {
    
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;
    
    public ProviderService(ProviderRepository providerRepository, ProviderMapper providerMapper) {
        this.providerRepository = providerRepository;
        this.providerMapper = providerMapper;
    }
    
    public ProviderDTO create(ProviderDTO dto) {
        if (providerRepository.existsByTaxId(dto.getTaxId())) {
            throw new RuntimeException("Provider with tax ID already exists");
        }
        Provider provider = providerMapper.toEntity(dto);
        return providerMapper.toDTO(providerRepository.save(provider));
    }
    
    public ProviderDTO update(Long id, ProviderDTO dto) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        
        if (!provider.getTaxId().equals(dto.getTaxId()) && 
            providerRepository.existsByTaxId(dto.getTaxId())) {
            throw new RuntimeException("Tax ID already exists");
        }
        
        provider.setName(dto.getName());
        provider.setTaxId(dto.getTaxId());
        provider.setEmail(dto.getEmail());
        provider.setAddress(dto.getAddress());
        
        return providerMapper.toDTO(providerRepository.save(provider));
    }
    
    @Transactional(readOnly = true)
    public ProviderDTO findById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        return providerMapper.toDTO(provider);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ProviderDTO> findAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Provider> providerPage;
        
        if (search != null && !search.trim().isEmpty()) {
            providerPage = providerRepository.search(search.trim(), pageable);
        } else {
            providerPage = providerRepository.findAll(pageable);
        }
        
        List<ProviderDTO> content = providerPage.getContent().stream()
                .map(providerMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                providerPage.getNumber(),
                providerPage.getSize(),
                providerPage.getTotalElements(),
                providerPage.getTotalPages(),
                providerPage.isFirst(),
                providerPage.isLast()
        );
    }
    
    public void delete(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new RuntimeException("Provider not found");
        }
        providerRepository.deleteById(id);
    }
}




