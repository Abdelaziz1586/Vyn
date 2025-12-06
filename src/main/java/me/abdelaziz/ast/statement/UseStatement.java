package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.util.Importer;

public final class UseStatement implements Statement {

    private final String filePath;

    public UseStatement(final String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void execute(final Environment env) {
        Importer.load(filePath, env);
    }
}