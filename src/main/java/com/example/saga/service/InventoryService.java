package com.example.saga.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {

    private final Map<String, Integer> availableStock = new ConcurrentHashMap<>();
    private final Map<UUID, Reservation> reservations = new ConcurrentHashMap<>();

    public InventoryService(Map<String, Integer> initialStock) {
        availableStock.putAll(initialStock);
    }

    public boolean reserve(UUID orderId, String productId, int quantity) {
        synchronized (availableStock) {
            int available = availableStock.getOrDefault(productId, 0);
            if (available < quantity) {
                return false;
            }
            availableStock.put(productId, available - quantity);
            reservations.put(orderId, new Reservation(productId, quantity));
            return true;
        }
    }

    public void release(UUID orderId) {
        Reservation reservation = reservations.remove(orderId);
        if (reservation != null) {
            availableStock.merge(reservation.productId(), reservation.quantity(), Integer::sum);
        }
    }

    public Map<String, Integer> snapshot() {
        return Map.copyOf(availableStock);
    }

    public record Reservation(String productId, int quantity) {
    }
}
