package com.invoicing.controller;

import com.invoicing.dto.InvoiceDTO;
import com.invoicing.dto.PageResponse;
import com.invoicing.service.InvoiceService;
import com.invoicing.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    private final ReportService reportService;
    
    public InvoiceController(InvoiceService invoiceService, ReportService reportService) {
        this.invoiceService = invoiceService;
        this.reportService = reportService;
    }
    
    @PostMapping
    public ResponseEntity<InvoiceDTO> create(@Valid @RequestBody InvoiceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(dto));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<InvoiceDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(invoiceService.findAll(page, size, search));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> generateReport(@PathVariable Long id) {
        byte[] pdfBytes = reportService.generateInvoicePDF(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}

