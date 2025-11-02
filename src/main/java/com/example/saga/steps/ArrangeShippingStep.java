package com.example.saga.steps;

import com.example.saga.domain.Order;
import com.example.saga.domain.OrderSagaData;
import com.example.saga.service.OrderService;
import com.example.saga.service.ShippingService;
import com.example.saga.saga.SagaStep;
import com.example.saga.saga.SagaStepFailedException;

public class ArrangeShippingStep implements SagaStep<OrderSagaData> {

    private final ShippingService shippingService;
    private final OrderService orderService;

    public ArrangeShippingStep(ShippingService shippingService, OrderService orderService) {
        this.shippingService = shippingService;
        this.orderService = orderService;
    }

    @Override
    public String getName() {
        return "Arrange shipping";
    }

    @Override
    public void process(OrderSagaData data) {
        Order order = data.getOrder();
        try {
            String shipmentId = shippingService.scheduleShipment(order.getId(), data.getShippingAddress());
            data.setShipmentId(shipmentId);
            orderService.markCompleted(order, shipmentId);
        } catch (RuntimeException ex) {
            order.record("Shipping failed: " + ex.getMessage());
            throw new SagaStepFailedException("Failed to arrange shipping", ex);
        }
    }

    @Override
    public void compensate(OrderSagaData data) {
        if (data.getShipmentId() != null) {
            shippingService.cancelShipment(data.getOrder().getId());
            data.getOrder().record("Shipping cancelled for order");
            data.setShipmentId(null);
        }
    }
}
