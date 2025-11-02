package com.example.saga.domain;

public class OrderSagaData {

    private final Order order;
    private final String accountId;
    private final String shippingAddress;
    private boolean inventoryReserved;
    private boolean paymentCaptured;
    private String shipmentId;

    public OrderSagaData(Order order, String accountId, String shippingAddress) {
        this.order = order;
        this.accountId = accountId;
        this.shippingAddress = shippingAddress;
    }

    public Order getOrder() {
        return order;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public boolean isInventoryReserved() {
        return inventoryReserved;
    }

    public void setInventoryReserved(boolean inventoryReserved) {
        this.inventoryReserved = inventoryReserved;
    }

    public boolean isPaymentCaptured() {
        return paymentCaptured;
    }

    public void setPaymentCaptured(boolean paymentCaptured) {
        this.paymentCaptured = paymentCaptured;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }
}
