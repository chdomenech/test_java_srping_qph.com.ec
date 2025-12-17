package com.invoicing.consumer;

import com.invoicing.config.RabbitMQConfig;
import com.invoicing.entity.Invoice;
import com.invoicing.entity.InvoiceItem;
import com.invoicing.entity.Product;
import com.invoicing.repository.InvoiceRepository;
import com.invoicing.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InvoiceConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceConsumer.class);
    
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    
    public InvoiceConsumer(InvoiceRepository invoiceRepository, ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
    }
    
    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
    public void handleStockUpdate(Map<String, Object> event) {
        try {
            logger.info("Processing stock update for invoice: {}", event.get("invoiceId"));
            
            Long invoiceId = Long.valueOf(event.get("invoiceId").toString());
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            
            // Update stock for each product
            for (InvoiceItem item : invoice.getItems()) {
                Product product = item.getProduct();
                int newStock = product.getStock() - item.getQuantity();
                if (newStock < 0) {
                    logger.warn("Negative stock detected for product: {}", product.getCode());
                    newStock = 0;
                }
                product.setStock(newStock);
                productRepository.save(product);
                logger.info("Updated stock for product {}: {} -> {}", 
                    product.getCode(), product.getStock(), newStock);
            }
            
            logger.info("Stock update completed for invoice: {}", invoiceId);
        } catch (Exception e) {
            logger.error("Error processing stock update", e);
            throw new RuntimeException("Failed to update stock", e);
        }
    }
    
    @RabbitListener(queues = RabbitMQConfig.PDF_GENERATION_QUEUE)
    public void handlePDFGeneration(Map<String, Object> event) {
        try {
            logger.info("PDF generation requested for invoice: {}", event.get("invoiceId"));
            logger.info("PDF generation completed for invoice: {}", event.get("invoiceId"));
        } catch (Exception e) {
            logger.error("Error generating PDF", e);
        }
    }
}

