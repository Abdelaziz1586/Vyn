package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.VynClass;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import java.util.List;

public final class ClassDeclaration implements Statement {

    private final String name;
    private final String parentName;
    private final List<Statement> body;

    public ClassDeclaration(final String name, final String parentName, final List<Statement> body) {
        this.name = name;
        this.parentName = parentName;
        this.body = body;
    }

    @Override
    public void execute(final Environment env) {
        env.define(name, new Value(new VynClass(name, parentName, body, env)), true);
    }
}