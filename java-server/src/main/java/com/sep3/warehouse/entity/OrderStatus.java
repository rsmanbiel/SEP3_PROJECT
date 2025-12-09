package com.sep3.warehouse.entity;

/**
 * Enum representing the possible states of an order.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    READY_FOR_SHIPMENT,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED
}
