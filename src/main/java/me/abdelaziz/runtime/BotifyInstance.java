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
        final Map<String, Value> vars = fields.getVariables();
        final StringBuilder sb = new StringBuilder("{");
        int i = 0;

        for (final Map.Entry<String, Value> entry : vars.entrySet()) {
            if (i > 0) sb.append(", ");

            sb.append(entry.getKey()).append("=");

            final Value val = entry.getValue();

            if (val.asJavaObject() == this) {
                sb.append("<me>");
            } else {
                sb.append(val);
            }

            i++;
        }

        sb.append("}");
        return sb.toString();
    }
}