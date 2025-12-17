package com.invoicing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "doc_number", nullable = false, unique = true)
    private String docNumber;

    @Column(nullable = false)
    private String email;

    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Invoice> invoices;
}

