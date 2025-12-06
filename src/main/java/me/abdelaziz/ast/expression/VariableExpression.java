package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class VariableExpression implements Expression {

    private final String name;

    public VariableExpression(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Value evaluate(final Environment env) {
        return env.get(name);
    }
}