package com.sep3.warehouse.entity;

/**
 * Enum representing types of inventory transactions.
 */
public enum TransactionType {
    PURCHASE,      // Stock received from supplier
    SALE,          // Stock sold to customer
    ADJUSTMENT,    // Manual adjustment
    RETURN,        // Customer return
    TRANSFER,      // Transfer between locations
    DAMAGED,       // Damaged/lost inventory
    RESERVED       // Reserved for order
}
