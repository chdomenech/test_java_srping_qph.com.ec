package com.invoicing.service;

import com.invoicing.entity.Invoice;
import com.invoicing.entity.InvoiceItem;
import com.invoicing.repository.InvoiceRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    
    private final InvoiceRepository invoiceRepository;
    
    public ReportService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }
    
    public byte[] generateInvoicePDF(Long invoiceId) {
        try {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
            
            // Load template
            ClassPathResource resource = new ClassPathResource("reports/invoice.jrxml");
            InputStream templateStream = resource.getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
            
            // Prepare parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoiceId", invoice.getId());
            parameters.put("customerName", invoice.getCustomer().getName());
            parameters.put("customerDoc", invoice.getCustomer().getDocNumber());
            parameters.put("customerEmail", invoice.getCustomer().getEmail());
            parameters.put("customerAddress", invoice.getCustomer().getAddress());
            parameters.put("providerName", invoice.getProvider().getName());
            parameters.put("providerTaxId", invoice.getProvider().getTaxId());
            parameters.put("providerEmail", invoice.getProvider().getEmail());
            parameters.put("providerAddress", invoice.getProvider().getAddress());
            parameters.put("issueDate", invoice.getIssueDate().toString());
            parameters.put("subtotal", invoice.getSubtotal());
            parameters.put("taxTotal", invoice.getTaxTotal());
            parameters.put("total", invoice.getTotal());
            
            // Prepare data source for items
            List<Map<String, Object>> items = invoice.getItems().stream()
                    .map(this::mapItem)
                    .collect(Collectors.toList());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);
            
            // Generate PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, dataSource);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
    
    private Map<String, Object> mapItem(InvoiceItem item) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("productCode", item.getProduct().getCode());
        itemMap.put("productName", item.getProduct().getName());
        itemMap.put("quantity", item.getQuantity());
        itemMap.put("unitPrice", item.getUnitPrice());
        itemMap.put("taxRate", item.getTaxRate());
        itemMap.put("lineTotal", item.getLineTotal());
        return itemMap;
    }
}




