package com.example.saga.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShippingService {

    private final Map<UUID, String> shipments = new ConcurrentHashMap<>();

    public String scheduleShipment(UUID orderId, String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (address.toLowerCase().contains("blocked")) {
            throw new IllegalStateException("Carrier refused the shipping address");
        }
        String shipmentId = "SHIP-" + Math.abs(address.hashCode() ^ orderId.hashCode());
        shipments.put(orderId, shipmentId);
        return shipmentId;
    }

    public void cancelShipment(UUID orderId) {
        shipments.remove(orderId);
    }
}
