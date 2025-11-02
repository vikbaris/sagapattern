package com.example.saga.service;

import com.example.saga.domain.Order;
import com.example.saga.domain.OrderStatus;

import java.time.LocalDateTime;

public class OrderService {

    public void markPending(Order order) {
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.record("Order marked pending");
    }

    public void markFailed(Order order, String reason) {
        order.setStatus(OrderStatus.FAILED);
        order.record("Order failed: " + reason);
    }

    public void markCompleted(Order order, String shipmentId) {
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        order.record("Order completed; shipment id " + shipmentId);
    }
}
