package com.invoicing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId;

    private String email;
    private String address;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Invoice> invoices;
}



