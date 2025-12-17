package com.invoicing.service;

import com.invoicing.dto.PageResponse;
import com.invoicing.dto.ProductDTO;
import com.invoicing.entity.Product;
import com.invoicing.mapper.ProductMapper;
import com.invoicing.repository.ProductRepository;
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
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    
    public ProductDTO create(ProductDTO dto) {
        if (productRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Product with code already exists");
        }
        Product product = productMapper.toEntity(dto);
        return productMapper.toDTO(productRepository.save(product));
    }
    
    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getCode().equals(dto.getCode()) && 
            productRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Product code already exists");
        }
        
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setTaxRate(dto.getTaxRate());
        product.setStock(dto.getStock());
        
        return productMapper.toDTO(productRepository.save(product));
    }
    
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toDTO(product);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ProductDTO> findAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> productPage;
        
        if (search != null && !search.trim().isEmpty()) {
            productPage = productRepository.search(search.trim(), pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        List<ProductDTO> content = productPage.getContent().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }
    
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> findAvailableProducts() {
        return productRepository.findAvailableProducts().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
}




