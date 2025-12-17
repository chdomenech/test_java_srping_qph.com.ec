package com.invoicing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDTO {
    private Long id;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String productName;
    private String productCode;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Tax rate is required")
    private BigDecimal taxRate;
    
    @NotNull(message = "Line total is required")
    private BigDecimal lineTotal;
}



