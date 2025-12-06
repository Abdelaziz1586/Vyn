package me.abdelaziz.runtime.function.exception;

public final class ContinueException extends RuntimeException {
    public ContinueException() {
        super(null, null, false, false);
    }
}