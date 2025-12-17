package com.invoicing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal taxTotal;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items;

    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, CANCELLED
    }
}

