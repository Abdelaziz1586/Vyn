package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.clazz.VynCatalog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CatalogStatement implements Statement {

    private final String name;
    private final List<String> entries;

    public CatalogStatement(final String name, final List<String> entries) {
        this.name = name;
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public List<String> getEntries() {
        return entries;
    }

    @Override
    public void execute(final Environment env) {
        final Map<String, Value> catalogEntries = new LinkedHashMap<>();

        for (final String entry : entries)
            catalogEntries.put(entry, new Value(entry));

        env.define(name, new Value(new VynCatalog(name, catalogEntries)), true);
    }
}
