package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import java.util.List;

public final class ClassDeclaration implements Statement {

    private final String name;
    private final List<Statement> body;

    public ClassDeclaration(final String name, final List<Statement> body) {
        this.name = name; this.body = body;
    }

    @Override public void execute(final Environment env) {
        env.define(name, new Value(body), true);
    }
}