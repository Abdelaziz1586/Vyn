package me.abdelaziz.runtime.function;

import me.abdelaziz.runtime.Value;

public final class ReturnException extends RuntimeException {

    public final Value value;

    public ReturnException(final Value value) {
        super(null, null, false, false);
        this.value = value;
    }
}