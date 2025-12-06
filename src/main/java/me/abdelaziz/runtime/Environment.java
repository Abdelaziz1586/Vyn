package me.abdelaziz.runtime;

import java.util.HashMap;
import java.util.Map;

public final class Environment {

    private final Environment parent;
    private final Map<String, Value> values;
    private final Map<String, Boolean> immutable;

    public Environment(final Environment parent) {
        this.parent = parent;
        this.values = new HashMap<>();
        this.immutable = new HashMap<>();
    }

    public void define(final String name, final Value value, final boolean isConstant) {
        if (values.containsKey(name)) throw new RuntimeException("Variable '" + name + "' already defined.");
        values.put(name, value);
        immutable.put(name, isConstant);
    }

    public void assign(final String name, final Value value) {
        if (values.containsKey(name)) {
            if (immutable.get(name)) throw new RuntimeException("Cannot reassign constant '" + name + "'");
            values.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value);
        } else {
            throw new RuntimeException("Undefined variable '" + name + "'");
        }
    }

    public Value get(final String name) {
        if (values.containsKey(name)) return values.get(name);
        if (parent != null) return parent.get(name);
        throw new RuntimeException("Undefined variable '" + name + "'");
    }

    public boolean has(final String name) {
        if (values.containsKey(name))
            return true;

        if (parent != null)
            return parent.has(name);

        return false;
    }

    public Map<String, Value> getVariables() {
        return new HashMap<>(values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}