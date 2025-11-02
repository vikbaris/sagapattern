package com.example.saga;

import com.example.saga.domain.Order;
import com.example.saga.domain.OrderSagaData;
import com.example.saga.domain.OrderStatus;
import com.example.saga.service.InventoryService;
import com.example.saga.service.OrderService;
import com.example.saga.service.PaymentService;
import com.example.saga.service.ShippingService;
import com.example.saga.saga.SagaOrchestrator;
import com.example.saga.saga.SagaStepFailedException;
import com.example.saga.steps.ArrangeShippingStep;
import com.example.saga.steps.ProcessPaymentStep;
import com.example.saga.steps.ReserveInventoryStep;
import com.example.saga.steps.StartOrderStep;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Scenario scenario = Scenario.fromArgs(args);

        OrderService orderService = new OrderService();
        InventoryService inventoryService = new InventoryService(
                Map.of("ethiopian-coffee-beans", scenario.availableInventory));
        PaymentService paymentService = new PaymentService(
                Map.of("premium-account", scenario.accountBalance));
        ShippingService shippingService = new ShippingService();

        Order order = new Order("ethiopian-coffee-beans", 2, 18.50);
        OrderSagaData sagaData = new OrderSagaData(order, "premium-account", scenario.shippingAddress);

        SagaOrchestrator<OrderSagaData> orchestrator = new SagaOrchestrator<>(
                List.of(
                        new StartOrderStep(orderService),
                        new ReserveInventoryStep(inventoryService),
                        new ProcessPaymentStep(paymentService),
                        new ArrangeShippingStep(shippingService, orderService)
                )
        );

        System.out.println("Running coffee order saga with scenario: " + scenario.keyword);
        try {
            orchestrator.execute(sagaData);
            System.out.println("Saga completed successfully");
        } catch (SagaStepFailedException failure) {
            orderService.markFailed(order, failure.getMessage());
            System.out.println("Saga failed: " + failure.getMessage());
        }

        reportOutcome(order, inventoryService, paymentService);
    }

    private static void reportOutcome(Order order, InventoryService inventoryService, PaymentService paymentService) {
        System.out.println();
        System.out.println("=== Final Order State ===");
        System.out.println("Order ID      : " + order.getId());
        System.out.println("Status        : " + order.getStatus());
        System.out.println("Timeline:");
        order.getTimeline().forEach(event -> System.out.println("  - " + event));

        System.out.println();
        System.out.println("Inventory snapshot: " + inventoryService.snapshot());
        System.out.println("Customer balances: " + paymentService.snapshot());
        if (order.getStatus() == OrderStatus.COMPLETED) {
            System.out.println("Coffee order saga finished; beans on the way!");
        } else {
            System.out.println("Order reverted; customer not charged.");
        }
    }

    private enum Scenario {
        SUCCESS("success", 20, 200.00, "42 Roast Ave, Bean City"),
        LOW_BALANCE("low-balance", 20, 10.00, "42 Roast Ave, Bean City"),
        OUT_OF_STOCK("out-of-stock", 1, 200.00, "42 Roast Ave, Bean City"),
        BLOCKED_ADDRESS("blocked-address", 20, 200.00, "100 Blocked Street");

        private final String keyword;
        private final int availableInventory;
        private final double accountBalance;
        private final String shippingAddress;

        Scenario(String keyword, int availableInventory, double accountBalance, String shippingAddress) {
            this.keyword = keyword;
            this.availableInventory = availableInventory;
            this.accountBalance = accountBalance;
            this.shippingAddress = shippingAddress;
        }

        private static Scenario fromArgs(String[] args) {
            if (args == null || args.length == 0) {
                return SUCCESS;
            }
            String key = args[0].trim().toLowerCase();
            for (Scenario scenario : values()) {
                if (scenario.keyword.equals(key)) {
                    return scenario;
                }
            }
            System.out.println("Unknown scenario '" + key + "', defaulting to success.");
            return SUCCESS;
        }
    }
}
