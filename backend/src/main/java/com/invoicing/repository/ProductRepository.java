package com.invoicing.repository;

import com.invoicing.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCode(String code);
    Optional<Product> findByCode(String code);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Product> search(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0 ORDER BY p.name")
    List<Product> findAvailableProducts();
}

