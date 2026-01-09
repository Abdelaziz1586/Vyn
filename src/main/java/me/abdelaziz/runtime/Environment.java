package me.abdelaziz.runtime;

import me.abdelaziz.runtime.clazz.VynClass;
import me.abdelaziz.runtime.clazz.nat.NativeClass;
import me.abdelaziz.runtime.function.OverloadedFunction;
import me.abdelaziz.runtime.function.VynCallable;

import java.util.Collections;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Environment parent;

    private Map<String, Value> values;
    private Map<String, Boolean> immutable;
    private Map<String, VynCallable> functions;

    public Environment(final Environment parent) {
        this.parent = parent;
    }

    public void define(final String name, final Value value, final boolean isConstant) {
        if (values == null)
            values = new HashMap<>();
        if (immutable == null)
            immutable = new HashMap<>();

        if (values.containsKey(name))
            throw new RuntimeException("Variable '" + name + "' already defined.");

        values.put(name, value);
        immutable.put(name, isConstant);
    }

    public void defineOrAssign(final String name, final Value value, final boolean isConstant) {
        if (isConstant) {
            define(name, value, true);
            return;
        }

        if (update(name, value))
            return;

        define(name, value, false);
    }

    private boolean update(final String name, final Value value) {
        if (values != null && values.containsKey(name)) {
            if (immutable != null && Boolean.TRUE.equals(immutable.get(name)))
                throw new RuntimeException("Cannot reassign constant '" + name + "'");

            values.put(name, value);
            return true;
        }

        if (parent != null)
            return parent.update(name, value);

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
        if (values != null) {
            final Value val = values.get(name);
            if (val != null)
                return val;
        }
        if (parent != null)
            return parent.get(name);

        throw new RuntimeException("Undefined variable '" + name + "'");
    }

    public boolean has(final String name) {
        if (values != null && values.containsKey(name))
            return true;
        if (functions != null && functions.containsKey(name))
            return true;

        if (parent != null)
            return parent.has(name);

        return false;
    }

    public Map<String, Value> getVariables() {
        return values != null ? new HashMap<>(values) : new HashMap<>();
    }

    public boolean hasFunction(final String name) {
        if (functions != null && functions.containsKey(name))
            return true;
        if (parent != null)
            return parent.hasFunction(name);
        return false;
    }

    public void defineFunction(final String name, final VynCallable function) {
        if (functions == null)
            functions = new HashMap<>();

        final VynCallable saved = functions.get(name);
        if (saved == null) {
            functions.put(name, function);
            return;
        }

        final VynCallable existing = functions.get(name);
        final OverloadedFunction overloaded;

        if (existing instanceof OverloadedFunction) {
            overloaded = (OverloadedFunction) existing;
        } else {
            overloaded = new OverloadedFunction();
            overloaded.addFunction(existing);
            functions.put(name, overloaded);
        }

        overloaded.addFunction(function);
    }

    public VynCallable getFunction(final String name) {
        if (functions != null) {
            final VynCallable func = functions.get(name);
            if (func != null)
                return func;
        }
        if (parent != null)
            return parent.getFunction(name);

        throw new RuntimeException("Undefined task '" + name + "'");
    }

    public void defineClass(final String name, final NativeClass nativeClass) {
        define(name, new Value(new VynClass(name, null, Collections.singletonList(nativeClass::execute), this)), true);
    }

    @Override
    public String toString() {
        return values != null ? values.toString() : "{}";
    }
}