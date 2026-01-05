package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class BinaryExpression implements Expression {

    private final String operator;
    private final Expression left, right;

    public BinaryExpression(final Expression left, final String operator, final Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value l = left.evaluate(env);
        final Value r = right.evaluate(env);

        switch (operator) {
            case "+":
                // Fast path: check strings first since instanceof is optimized by JVM
                final Object lObj = l.asJavaObject();
                final Object rObj = r.asJavaObject();
                if (lObj instanceof String || rObj instanceof String)
                    return new Value(l.toString() + r);

                return new Value(l.asDouble() + r.asDouble());
            case "-":
                return new Value(l.asDouble() - r.asDouble());
            case "*":
                return new Value(l.asDouble() * r.asDouble());
            case "/":
                return new Value(l.asDouble() / r.asDouble());
            case "%":
                return new Value(l.asDouble() % r.asDouble());
            case ">":
                return new Value(l.asDouble() > r.asDouble());
            case "<":
                return new Value(l.asDouble() < r.asDouble());
            case "==":
                return new Value(l.equals(r));
            case "!=":
                return new Value(!l.equals(r));
            default:
                throw new RuntimeException("Unknown operator");
        }
    }
}