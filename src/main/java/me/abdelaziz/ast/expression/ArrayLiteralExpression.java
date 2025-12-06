package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.ArrayList;
import java.util.List;

public final class ArrayLiteralExpression implements Expression {

    private final List<Expression> elements;

    public ArrayLiteralExpression(final List<Expression> elements) {
        this.elements = elements;
    }

    public List<Expression> getElements() {
        return elements;
    }

    @Override
    public Value evaluate(final Environment env) {
        final List<Value> values = new ArrayList<>();
        for (final Expression expr : elements)
            values.add(expr.evaluate(env));

        return new Value(values);
    }
}