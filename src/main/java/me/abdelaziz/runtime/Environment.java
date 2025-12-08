package me.abdelaziz.runtime;

import me.abdelaziz.runtime.function.BotifyCallable;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    private final Environment parent;

    private Map<String, Value> values;
    private Map<String, Boolean> immutable;
    private Map<String, BotifyCallable> functions;

    public Environment(final Environment parent) {
        this.parent = parent;
    }

    public void define(final String name, final Value value, final boolean isConstant) {
        if (values == null) values = new HashMap<>();
        if (immutable == null) immutable = new HashMap<>();

        if (values.containsKey(name)) throw new RuntimeException("Variable '" + name + "' already defined.");

        values.put(name, value);
        immutable.put(name, isConstant);
    }

    public void defineOrAssign(final String name, final Value value, final boolean isConstant) {
        if (isConstant) {
            define(name, value, true);
            return;
        }

        if (update(name, value)) return;

        define(name, value, false);
    }

    private boolean update(final String name, final Value value) {
        if (values != null && values.containsKey(name)) {
            if (immutable != null && Boolean.TRUE.equals(immutable.get(name)))
                throw new RuntimeException("Cannot reassign constant '" + name + "'");

            values.put(name, value);
            return true;
        }

        if (parent != null) return parent.update(name, value);

        return false;
    }

    public void assign(final String name, final Value value) {
        if (values != null && values.containsKey(name)) {
            if (immutable != null && Boolean.TRUE.equals(immutable.get(name)))
                throw new RuntimeException("Cannot reassign constant '" + name + "'");

            values.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value);
        } else {
            throw new RuntimeException("Undefined variable '" + name + "'");
        }
    }

    public Value get(final String name) {
        if (values != null && values.containsKey(name)) return values.get(name);
        if (parent != null) return parent.get(name);

        throw new RuntimeException("Undefined variable '" + name + "'");
    }

    public Map<String, Value> getVariables() {
        return values != null ? new HashMap<>(values) : new HashMap<>();
    }

    public void defineFunction(final String name, final BotifyCallable function) {
        if (functions == null) functions = new HashMap<>();
        functions.put(name, function);
    }

    public BotifyCallable getFunction(final String name) {
        if (functions != null && functions.containsKey(name)) return functions.get(name);
        if (parent != null) return parent.getFunction(name);

        throw new RuntimeException("Undefined task '" + name + "'");
    }

    @Override
    public String toString() {
        return values != null ? values.toString() : "{}";
    }
}