package com.example.saga.steps;

import com.example.saga.domain.Order;
import com.example.saga.domain.OrderSagaData;
import com.example.saga.service.PaymentService;
import com.example.saga.saga.SagaStep;
import com.example.saga.saga.SagaStepFailedException;

public class ProcessPaymentStep implements SagaStep<OrderSagaData> {

    private final PaymentService paymentService;

    public ProcessPaymentStep(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String getName() {
        return "Capture payment";
    }

    @Override
    public void process(OrderSagaData data) {
        Order order = data.getOrder();
        double amount = order.totalPrice();
        boolean charged = paymentService.charge(order.getId(), data.getAccountId(), amount);
        if (!charged) {
            order.record("Payment capture failed");
            throw new SagaStepFailedException("Insufficient funds for account " + data.getAccountId());
        }
        data.setPaymentCaptured(true);
        order.record("Captured payment " + amount + " from account " + data.getAccountId());
    }

    @Override
    public void compensate(OrderSagaData data) {
        if (data.isPaymentCaptured()) {
            paymentService.refund(data.getOrder().getId(), data.getAccountId());
            data.setPaymentCaptured(false);
            data.getOrder().record("Payment refunded to account " + data.getAccountId());
        }
    }
}
