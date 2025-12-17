package com.invoicing.service;

import com.invoicing.dto.AnomalyResponse;
import com.invoicing.dto.ProductDTO;
import com.invoicing.dto.RecommendationResponse;
import com.invoicing.entity.Invoice;
import com.invoicing.entity.Product;
import com.invoicing.mapper.ProductMapper;
import com.invoicing.repository.InvoiceRepository;
import com.invoicing.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AIService {
    
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    public AIService(InvoiceRepository invoiceRepository,
                    ProductRepository productRepository,
                    ProductMapper productMapper) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    
    public RecommendationResponse getRecommendations(Long customerId) {
        // Get customer's purchase history
        List<Invoice> customerInvoices = invoiceRepository.findByCustomerId(customerId);
        
        // Count product frequencies
        Map<Long, Integer> productFrequency = new HashMap<>();
        for (Invoice invoice : customerInvoices) {
            invoice.getItems().forEach(item -> {
                Long productId = item.getProduct().getId();
                productFrequency.put(productId, 
                    productFrequency.getOrDefault(productId, 0) + item.getQuantity());
            });
        }
        
        // Get top 3 most purchased products
        List<Long> topProductIds = productFrequency.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // Find similar products (same category or price range)
        List<Product> recommendedProducts = new ArrayList<>();
        if (!topProductIds.isEmpty()) {
            List<Product> topProducts = productRepository.findAllById(topProductIds);
            for (Product topProduct : topProducts) {
                // Find products in similar price range (±20%)
                BigDecimal minPrice = topProduct.getPrice().multiply(BigDecimal.valueOf(0.8));
                BigDecimal maxPrice = topProduct.getPrice().multiply(BigDecimal.valueOf(1.2));
                
                List<Product> similar = productRepository.findAll().stream()
                        .filter(p -> !topProductIds.contains(p.getId()))
                        .filter(p -> p.getPrice().compareTo(minPrice) >= 0 && 
                                   p.getPrice().compareTo(maxPrice) <= 0)
                        .filter(p -> p.getStock() > 0)
                        .limit(2)
                        .collect(Collectors.toList());
                
                recommendedProducts.addAll(similar);
            }
        }
        
        // If no recommendations, get top available products
        if (recommendedProducts.isEmpty()) {
            recommendedProducts = productRepository.findAvailableProducts().stream()
                    .limit(5)
                    .collect(Collectors.toList());
        }
        
        // Remove duplicates and limit to 5
        recommendedProducts = recommendedProducts.stream()
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
        
        List<ProductDTO> productDTOs = recommendedProducts.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
        
        String reason = customerInvoices.isEmpty() 
                ? "Based on popular products" 
                : "Based on your purchase history";
        
        return new RecommendationResponse(productDTOs, reason);
    }
    
    public AnomalyResponse detectAnomaly(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        // Calculate average invoice amount for this customer
        List<Invoice> customerInvoices = invoiceRepository.findByCustomerId(invoice.getCustomer().getId());
        
        if (customerInvoices.size() < 2) {
            return new AnomalyResponse(0.0, "Insufficient history for anomaly detection");
        }
        
        BigDecimal avgAmount = customerInvoices.stream()
                .filter(i -> !i.getId().equals(invoiceId))
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(customerInvoices.size() - 1), 2, RoundingMode.HALF_UP);
        
        BigDecimal currentAmount = invoice.getTotal();
        BigDecimal deviation = currentAmount.subtract(avgAmount).abs();
        BigDecimal deviationPercent = avgAmount.compareTo(BigDecimal.ZERO) > 0 
                ? deviation.divide(avgAmount, 4, RoundingMode.HALF_UP)
                : BigDecimal.ONE;
        
        // Calculate anomaly score (0-1)
        double score = Math.min(deviationPercent.doubleValue(), 1.0);
        
        String explanation;
        if (score > 0.5) {
            explanation = String.format(
                "High deviation detected: Current amount (%.2f) is %.1f%% different from average (%.2f)",
                currentAmount, deviationPercent.multiply(BigDecimal.valueOf(100)).doubleValue(), avgAmount
            );
        } else if (score > 0.3) {
            explanation = String.format(
                "Moderate deviation: Current amount (%.2f) is %.1f%% different from average (%.2f)",
                currentAmount, deviationPercent.multiply(BigDecimal.valueOf(100)).doubleValue(), avgAmount
            );
        } else {
            explanation = "No significant anomalies detected";
        }
        
        return new AnomalyResponse(score, explanation);
    }
}



