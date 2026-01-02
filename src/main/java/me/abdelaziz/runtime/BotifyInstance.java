package me.abdelaziz.runtime;

import me.abdelaziz.runtime.function.BotifyCallable;

import java.util.Map;

public final class BotifyInstance {

    private final Environment fields;

    public BotifyInstance(final Environment classScope) {
        this.fields = classScope;
    }

    public static BotifyInstance fromHost(final BotifyClass botifyClass, final Object host) {
        final Environment instanceEnv = new Environment(botifyClass.getClosure());
        final BotifyInstance instance = new BotifyInstance(instanceEnv);

        instanceEnv.define("me", new Value(instance), true);
        instanceEnv.define("__host__", new Value(host), true);

        for (final me.abdelaziz.ast.Statement stmt : botifyClass.getBody())
            stmt.execute(instanceEnv);

        return instance;
    }

    public Value get(final String name) {
        return fields.get(name);
    }

    public boolean has(final String name) {
        return fields.has(name);
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