package com.invoicing.service;

import com.invoicing.entity.Invoice;
import com.invoicing.entity.InvoiceItem;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvoiceEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${spring.rabbitmq.template.exchange:invoice.exchange}")
    private String exchange;
    
    @Value("${spring.rabbitmq.template.routing-key:invoice.created}")
    private String routingKey;
    
    public InvoiceEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void publishInvoiceCreated(Invoice invoice) {
        Map<String, Object> event = new HashMap<>();
        event.put("invoiceId", invoice.getId());
        event.put("customerId", invoice.getCustomer().getId());
        event.put("providerId", invoice.getProvider().getId());
        event.put("total", invoice.getTotal());
        event.put("issueDate", invoice.getIssueDate().toString());
        
        List<Map<String, Object>> items = invoice.getItems().stream()
                .map(this::mapItem)
                .collect(Collectors.toList());
        event.put("items", items);
        
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
    
    private Map<String, Object> mapItem(InvoiceItem item) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("productId", item.getProduct().getId());
        itemMap.put("productCode", item.getProduct().getCode());
        itemMap.put("quantity", item.getQuantity());
        itemMap.put("unitPrice", item.getUnitPrice());
        itemMap.put("lineTotal", item.getLineTotal());
        return itemMap;
    }
}




