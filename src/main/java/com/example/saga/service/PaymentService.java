package com.example.saga.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    private final Map<String, Double> balances = new ConcurrentHashMap<>();
    private final Map<UUID, Double> capturedAmounts = new ConcurrentHashMap<>();

    public PaymentService(Map<String, Double> initialBalances) {
        balances.putAll(initialBalances);
    }

    public boolean charge(UUID orderId, String accountId, double amount) {
        synchronized (balances) {
            double balance = balances.getOrDefault(accountId, 0.0);
            if (balance < amount) {
                return false;
            }
            balances.put(accountId, balance - amount);
            capturedAmounts.put(orderId, amount);
            return true;
        }
    }

    public void refund(UUID orderId, String accountId) {
        Double amount = capturedAmounts.remove(orderId);
        if (amount != null) {
            balances.merge(accountId, amount, Double::sum);
        }
    }

    public Map<String, Double> snapshot() {
        return Map.copyOf(balances);
    }
}
