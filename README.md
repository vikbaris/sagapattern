# Coffee Order Saga Sample

This project demonstrates the **Saga orchestration pattern** in plain Java with a realistic e-commerce scenario.  
Placing a coffee order requires four distributed operations:

1. Mark the order as pending
2. Reserve inventory
3. Capture customer payment
4. Schedule shipping

If any operation fails, the saga orchestrator triggers compensating actions that roll the order back to a safe state (release inventory, refund the payment, cancel shipping).

## Running the sample

The project uses only the JDK; no external dependencies are required.

```bash
# Compile
javac -d out $(find src/main/java -name "*.java")

# Happy path run
java -cp out com.example.saga.Main

# Simulate different failure scenarios
java -cp out com.example.saga.Main low-balance      # payment failure with compensation
java -cp out com.example.saga.Main out-of-stock     # inventory failure with compensation
java -cp out com.example.saga.Main blocked-address  # shipping failure
```

Each run prints the saga log, final order status, and the state of supporting services so you can see both the forward and compensating paths of the saga.
