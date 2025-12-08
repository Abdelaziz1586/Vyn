package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;

public final class VarDeclaration implements Statement {

    private final String name;
    private final boolean isConstant;
    private final Expression initializer;

    public VarDeclaration(final String name, final Expression initializer, final boolean isConstant) {
        this.name = name;
        this.initializer = initializer;
        this.isConstant = isConstant;
    }

    @Override
    public void execute(final Environment env) {
        env.defineOrAssign(name, initializer.evaluate(env), isConstant);
    }
}