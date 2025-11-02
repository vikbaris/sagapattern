package com.example.saga.saga;

/**
 * Runtime exception indicating that a saga step failed and triggered compensation.
 */
public class SagaStepFailedException extends RuntimeException {

    public SagaStepFailedException(String message) {
        super(message);
    }

    public SagaStepFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
