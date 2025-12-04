package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class LiteralExpression implements Expression {
    private final Value value;

    public LiteralExpression(final Object value) {
        this.value = new Value(value);
    }

    @Override
    public Value evaluate(final Environment env) {
        return value;
    }
}