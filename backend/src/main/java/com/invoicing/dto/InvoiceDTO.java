package com.invoicing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private String customerName;
    
    @NotNull(message = "Provider ID is required")
    private Long providerId;
    
    private String providerName;
    
    private LocalDateTime issueDate;
    
    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;
    
    @NotNull(message = "Tax total is required")
    private BigDecimal taxTotal;
    
    @NotNull(message = "Total is required")
    private BigDecimal total;
    
    private String status;
    
    @NotEmpty(message = "Invoice must have at least one item")
    @Valid
    private List<InvoiceItemDTO> items;
}

