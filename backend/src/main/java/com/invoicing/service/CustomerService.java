package com.invoicing.service;

import com.invoicing.dto.CustomerDTO;
import com.invoicing.dto.PageResponse;
import com.invoicing.entity.Customer;
import com.invoicing.mapper.CustomerMapper;
import com.invoicing.repository.CustomerRepository;
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
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    public CustomerDTO create(CustomerDTO dto) {
        if (customerRepository.existsByDocNumber(dto.getDocNumber())) {
            throw new RuntimeException("Customer with document number already exists");
        }
        Customer customer = customerMapper.toEntity(dto);
        return customerMapper.toDTO(customerRepository.save(customer));
    }
    
    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if (!customer.getDocNumber().equals(dto.getDocNumber()) && 
            customerRepository.existsByDocNumber(dto.getDocNumber())) {
            throw new RuntimeException("Document number already exists");
        }
        
        customer.setName(dto.getName());
        customer.setDocNumber(dto.getDocNumber());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        
        return customerMapper.toDTO(customerRepository.save(customer));
    }
    
    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toDTO(customer);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<CustomerDTO> findAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Customer> customerPage;
        
        if (search != null && !search.trim().isEmpty()) {
            customerPage = customerRepository.search(search.trim(), pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }
        
        List<CustomerDTO> content = customerPage.getContent().stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isFirst(),
                customerPage.isLast()
        );
    }
    
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepository.deleteById(id);
    }
}




