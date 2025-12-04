package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;

public final class ExpressionStatement implements Statement {

    private final Expression expression;

    public ExpressionStatement(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(final Environment env) {
        expression.evaluate(env);
    }
}