package me.abdelaziz.runtime;

import me.abdelaziz.runtime.function.BotifyCallable;

import java.util.Map;

public final class BotifyInstance {

    private final Environment fields;

    public BotifyInstance(final Environment classScope) {
        this.fields = classScope;
    }

    public Value get(final String name) {
        return fields.get(name);
    }

    public BotifyCallable getMethod(final String name) {
        return fields.getFunction(name);
    }

    public void set(final String name, final Value value) {
        fields.assign(name, value);
    }

    public Map<String, Value> asMap() {
        return fields.getVariables();
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}