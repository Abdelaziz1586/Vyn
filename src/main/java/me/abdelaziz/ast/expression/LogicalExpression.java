package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class LogicalExpression implements Expression {

    private final Expression left, right;
    private final String operator;

    public LogicalExpression(final Expression left, final String operator, final Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value leftVal = left.evaluate(env);

        if (operator.equals("||")) {
            if (leftVal.asBoolean())
                return leftVal;
        } else {
            if (!leftVal.asBoolean())
                return leftVal;
        }

        return right.evaluate(env);
    }
}
