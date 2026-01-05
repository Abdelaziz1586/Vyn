package me.abdelaziz.runtime;

import me.abdelaziz.runtime.clazz.VynClass;
import me.abdelaziz.runtime.function.VynCallable;

import java.util.Collections;
import java.util.Map;

public final class VynInstance {

    private final Environment fields;

    public VynInstance(final Environment classScope) {
        this.fields = classScope;
    }

    public static VynInstance fromHost(final VynClass vynClass, final Object host) {
        final Environment instanceEnv = new Environment(vynClass.getClosure());
        final VynInstance instance = new VynInstance(instanceEnv);

        instanceEnv.define("me", new Value(instance), true);
        instanceEnv.define("__host__", new Value(host), true);

        for (final me.abdelaziz.ast.Statement stmt : vynClass.getBody())
            stmt.execute(instanceEnv);

        return instance;
    }

    public Value get(final String name) {
        return fields.get(name);
    }

    public boolean has(final String name) {
        return fields.has(name);
    }

    public VynCallable getMethod(final String name) {
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
        // Ahmed 3amr's change
        if (fields.hasFunction("stringify")) {
            return fields.getFunction("stringify").call(fields, Collections.emptyList()).toString();
        }

        final Map<String, Value> vars = fields.getVariables();
        final StringBuilder sb = new StringBuilder("{");
        int i = 0;

        for (final Map.Entry<String, Value> entry : vars.entrySet()) {
            if (entry.getKey().startsWith("__"))
                continue;

            if (i > 0)
                sb.append(", ");
            sb.append(entry.getKey()).append("=");

            final Value val = entry.getValue();

            sb.append(
                    val.asJavaObject() == this
                            ? "<me>"
                            : val

            );

            i++;
        }

        sb.append("}");
        return sb.toString();
    }
}