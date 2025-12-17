package com.invoicing.controller;

import com.invoicing.dto.PageResponse;
import com.invoicing.dto.ProviderDTO;
import com.invoicing.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*")
public class ProviderController {
    
    private final ProviderService providerService;
    
    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }
    
    @PostMapping
    public ResponseEntity<ProviderDTO> create(@Valid @RequestBody ProviderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(providerService.create(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProviderDTO> update(@PathVariable Long id, 
                                              @Valid @RequestBody ProviderDTO dto) {
        return ResponseEntity.ok(providerService.update(id, dto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProviderDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.findById(id));
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<ProviderDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(providerService.findAll(page, size, search));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        providerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}



