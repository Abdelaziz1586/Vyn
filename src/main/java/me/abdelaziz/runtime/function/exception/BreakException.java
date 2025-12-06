package me.abdelaziz.runtime.function.exception;

public final class BreakException extends RuntimeException {

    public BreakException() {
        super(null, null, false, false);
    }
}