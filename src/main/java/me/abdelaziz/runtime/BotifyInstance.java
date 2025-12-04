package me.abdelaziz.runtime;

public final class BotifyInstance {

    private final Environment fields;

    public BotifyInstance(final Environment classScope) {
        this.fields = classScope;
    }

    public Value get(final String name) {
        return fields.get(name);
    }

    public void set(final String name, final Value value) {
        fields.assign(name, value);
    }

    @Override
    public String toString() {
        return fields.toString();
    }
}