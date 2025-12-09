package com.sep3.warehouse.repository;

import com.sep3.warehouse.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT oi.product.id, SUM(oi.quantity) FROM OrderItem oi " +
           "JOIN oi.order o WHERE o.status = 'DELIVERED' " +
           "GROUP BY oi.product.id ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts();
}
