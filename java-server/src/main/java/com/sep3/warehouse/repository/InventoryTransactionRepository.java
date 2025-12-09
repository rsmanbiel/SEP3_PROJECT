package com.sep3.warehouse.repository;

import com.sep3.warehouse.entity.InventoryTransaction;
import com.sep3.warehouse.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for InventoryTransaction entity.
 */
@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    
    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.id = :productId ORDER BY it.createdAt DESC")
    Page<InventoryTransaction> findByProductId(@Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = :type ORDER BY it.createdAt DESC")
    Page<InventoryTransaction> findByTransactionType(@Param("type") TransactionType type, Pageable pageable);
    
    @Query("SELECT it FROM InventoryTransaction it WHERE it.createdAt BETWEEN :startDate AND :endDate ORDER BY it.createdAt DESC")
    List<InventoryTransaction> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT it.transactionType, SUM(it.quantity) FROM InventoryTransaction it " +
           "WHERE it.product.id = :productId GROUP BY it.transactionType")
    List<Object[]> getTransactionSummaryByProduct(@Param("productId") Long productId);
}
