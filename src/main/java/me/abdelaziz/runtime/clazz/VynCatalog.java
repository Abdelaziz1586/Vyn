package me.abdelaziz.runtime.clazz;

import me.abdelaziz.runtime.Value;

import java.util.Map;

public final class VynCatalog {

    private final String name;
    private final Map<String, Value> entries;

    public VynCatalog(final String name, final Map<String, Value> entries) {
        this.name = name;
        this.entries = entries;
    }

    public Value get(final String entry) {
        if (!entries.containsKey(entry))
            throw new RuntimeException("Unknown catalog entry '" + entry + "' in '" + name + "'");

        return entries.get(entry);
    }

    @Override
    public String toString() {
        return "<catalog " + name + ">";
    }
}
