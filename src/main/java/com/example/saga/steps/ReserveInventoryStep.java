package com.example.saga.steps;

import com.example.saga.domain.Order;
import com.example.saga.domain.OrderSagaData;
import com.example.saga.service.InventoryService;
import com.example.saga.saga.SagaStep;
import com.example.saga.saga.SagaStepFailedException;

public class ReserveInventoryStep implements SagaStep<OrderSagaData> {

    private final InventoryService inventoryService;

    public ReserveInventoryStep(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public String getName() {
        return "Reserve inventory";
    }

    @Override
    public void process(OrderSagaData data) {
        Order order = data.getOrder();
        boolean reserved = inventoryService.reserve(order.getId(), order.getProductId(), order.getQuantity());
        if (!reserved) {
            order.record("Inventory reservation failed");
            throw new SagaStepFailedException("Not enough stock for product " + order.getProductId());
        }
        data.setInventoryReserved(true);
        order.record("Reserved " + order.getQuantity() + " units of " + order.getProductId());
    }

    @Override
    public void compensate(OrderSagaData data) {
        if (data.isInventoryReserved()) {
            inventoryService.release(data.getOrder().getId());
            data.setInventoryReserved(false);
            data.getOrder().record("Inventory reservation released");
        }
    }
}
