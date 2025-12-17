package com.invoicing.repository;

import com.invoicing.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    boolean existsByTaxId(String taxId);
    
    @Query("SELECT p FROM Provider p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.taxId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Provider> search(@Param("search") String search, Pageable pageable);
}



