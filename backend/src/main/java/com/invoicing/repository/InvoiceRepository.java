package com.invoicing.repository;

import com.invoicing.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT i FROM Invoice i WHERE " +
           "LOWER(i.customer.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.provider.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Invoice> search(@Param("search") String search, Pageable pageable);
    
    List<Invoice> findByCustomerId(Long customerId);
    
    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
}



