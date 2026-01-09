package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.function.exception.ReturnException;
import me.abdelaziz.runtime.Value;

public final class ReturnStatement implements Statement {

    private final Expression valueExpr;

    public ReturnStatement(final Expression valueExpr) {
        this.valueExpr = valueExpr;
    }

    public Expression getValueExpr() {
        return valueExpr;
    }

    @Override
    public void execute(final Environment env) {
        final Value result = valueExpr.evaluate(env);
        throw new ReturnException(result);
    }
}