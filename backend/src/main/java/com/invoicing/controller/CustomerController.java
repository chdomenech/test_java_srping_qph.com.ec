package com.invoicing.controller;

import com.invoicing.dto.CustomerDTO;
import com.invoicing.dto.PageResponse;
import com.invoicing.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private final CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, 
                                             @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<CustomerDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(customerService.findAll(page, size, search));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}



