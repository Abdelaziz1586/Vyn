package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class UnaryExpression implements Expression {

    private final String operator;
    private final Expression right;

    public UnaryExpression(final String operator, final Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value result = right.evaluate(env);

        switch (operator) {
            case "!":
                return new Value(!result.asBoolean());
            case "-":
                return new Value(-result.asDouble());
            default:
                throw new RuntimeException("Unknown unary operator: " + operator);
        }
    }
}