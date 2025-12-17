package com.invoicing.service;

import com.invoicing.dto.InvoiceDTO;
import com.invoicing.dto.InvoiceItemDTO;
import com.invoicing.dto.PageResponse;
import com.invoicing.entity.*;
import com.invoicing.mapper.InvoiceMapper;
import com.invoicing.repository.CustomerRepository;
import com.invoicing.repository.InvoiceRepository;
import com.invoicing.repository.ProductRepository;
import com.invoicing.repository.ProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProviderRepository providerRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceEventPublisher eventPublisher;
    
    public InvoiceService(InvoiceRepository invoiceRepository,
                         CustomerRepository customerRepository,
                         ProviderRepository providerRepository,
                         ProductRepository productRepository,
                         InvoiceMapper invoiceMapper,
                         InvoiceEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.providerRepository = providerRepository;
        this.productRepository = productRepository;
        this.invoiceMapper = invoiceMapper;
        this.eventPublisher = eventPublisher;
    }
    
    public InvoiceDTO create(InvoiceDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        
        Invoice invoice = Invoice.builder()
                .customer(customer)
                .provider(provider)
                .issueDate(dto.getIssueDate() != null ? dto.getIssueDate() : LocalDateTime.now())
                .status(Invoice.InvoiceStatus.ISSUED)
                .items(new ArrayList<>())
                .build();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        
        for (InvoiceItemDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));
            
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTaxRate(itemDTO.getTaxRate());
            
            BigDecimal lineSubtotal = itemDTO.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            BigDecimal lineTax = lineSubtotal.multiply(itemDTO.getTaxRate().divide(BigDecimal.valueOf(100)));
            BigDecimal lineTotal = lineSubtotal.add(lineTax);
            
            item.setLineTotal(lineTotal);
            if (invoice.getItems() == null) {
                invoice.setItems(new ArrayList<>());
            }
            invoice.getItems().add(item);
            
            subtotal = subtotal.add(lineSubtotal);
            taxTotal = taxTotal.add(lineTax);
        }
        
        invoice.setSubtotal(subtotal);
        invoice.setTaxTotal(taxTotal);
        invoice.setTotal(subtotal.add(taxTotal));
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Publish event
        eventPublisher.publishInvoiceCreated(savedInvoice);
        
        return invoiceMapper.toDTO(savedInvoice);
    }
    
    @Transactional(readOnly = true)
    public InvoiceDTO findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return invoiceMapper.toDTO(invoice);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<InvoiceDTO> findAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<Invoice> invoicePage;
        
        if (search != null && !search.trim().isEmpty()) {
            invoicePage = invoiceRepository.search(search.trim(), pageable);
        } else {
            invoicePage = invoiceRepository.findAll(pageable);
        }
        
        List<InvoiceDTO> content = invoicePage.getContent().stream()
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                invoicePage.getNumber(),
                invoicePage.getSize(),
                invoicePage.getTotalElements(),
                invoicePage.getTotalPages(),
                invoicePage.isFirst(),
                invoicePage.isLast()
        );
    }
    
    public void delete(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found");
        }
        invoiceRepository.deleteById(id);
    }
}

