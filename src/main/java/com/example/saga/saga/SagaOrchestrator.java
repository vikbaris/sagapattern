package com.example.saga.saga;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates saga steps sequentially. When a step fails, the orchestrator walks the already
 * completed steps in reverse order and invokes their compensating actions.
 *
 * @param <T> shared saga data passed across steps
 */
public class SagaOrchestrator<T> {

    private final List<SagaStep<T>> steps;

    public SagaOrchestrator(List<SagaStep<T>> steps) {
        this.steps = List.copyOf(Objects.requireNonNull(steps, "steps"));
    }

    /**
     * Execute the configured saga steps. Compensation is automatically triggered on failure.
     *
     * @param data shared saga data
     */
    public void execute(T data) {
        Deque<SagaStep<T>> completedSteps = new ArrayDeque<>();
        for (SagaStep<T> step : steps) {
            try {
                log("Starting step: " + step.getName());
                step.process(data);
                completedSteps.push(step);
                log("Completed step: " + step.getName());
            } catch (RuntimeException processingException) {
                log("Step failed: " + step.getName() + "; starting compensation");
                compensate(data, completedSteps, processingException);
                throw (processingException instanceof SagaStepFailedException)
                        ? processingException
                        : new SagaStepFailedException(step.getName() + " failed", processingException);
            }
        }
    }

    private void compensate(T data, Deque<SagaStep<T>> completedSteps, RuntimeException failure) {
        while (!completedSteps.isEmpty()) {
            SagaStep<T> stepToRollback = completedSteps.pop();
            try {
                log("Compensating step: " + stepToRollback.getName());
                stepToRollback.compensate(data);
            } catch (RuntimeException compensationException) {
                // In a production system the failure would be reported to an incident system.
                log("Compensation failed for step: " + stepToRollback.getName()
                        + " due to: " + compensationException.getMessage());
            }
        }
        log("Saga rolled back due to: " + failure.getMessage());
    }

    private void log(String message) {
        System.out.println("[Saga] " + message);
    }
}
