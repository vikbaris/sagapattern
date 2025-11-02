package com.example.saga.saga;

/**
 * Represents a single unit of work in a saga. Each step must define the action to perform and
 * a compensating action that reverts the work if a downstream step fails.
 *
 * @param <T> shared saga data passed across steps
 */
public interface SagaStep<T> {

    /**
     * @return human readable name used for logging and error reporting
     */
    String getName();

    /**
     * Carry out the business operation for this step.
     *
     * @param data shared saga data
     * @throws SagaStepFailedException when the step cannot be completed
     */
    void process(T data);

    /**
     * Perform a compensating action to undo the work of {@link #process(Object)}.
     *
     * @param data shared saga data
     */
    void compensate(T data);
}
