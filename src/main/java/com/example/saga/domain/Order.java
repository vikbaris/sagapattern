package com.example.saga.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final String productId;
    private final int quantity;
    private final double pricePerUnit;
    private OrderStatus status;
    private final List<String> timeline = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Order(String productId, int quantity, double pricePerUnit) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.status = OrderStatus.NEW;
    }

    public UUID getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double totalPrice() {
        return quantity * pricePerUnit;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<String> getTimeline() {
        return timeline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void record(String event) {
        timeline.add(LocalDateTime.now() + " - " + event);
    }
}
