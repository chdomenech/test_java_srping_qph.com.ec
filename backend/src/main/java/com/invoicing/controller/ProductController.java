package com.invoicing.controller;

import com.invoicing.dto.PageResponse;
import com.invoicing.dto.ProductDTO;
import com.invoicing.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, 
                                            @Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<ProductDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(productService.findAll(page, size, search));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ProductDTO>> findAvailable() {
        return ResponseEntity.ok(productService.findAvailableProducts());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}



