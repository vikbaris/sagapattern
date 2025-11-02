package com.example.saga.steps;

import com.example.saga.domain.OrderSagaData;
import com.example.saga.service.OrderService;
import com.example.saga.saga.SagaStep;

public class StartOrderStep implements SagaStep<OrderSagaData> {

    private final OrderService orderService;

    public StartOrderStep(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public String getName() {
        return "Mark order pending";
    }

    @Override
    public void process(OrderSagaData data) {
        orderService.markPending(data.getOrder());
    }

    @Override
    public void compensate(OrderSagaData data) {
        // Order stays in NEW status if later steps fail before completion.
    }
}
