package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.BotifyFunction;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.ArrayList;
import java.util.List;

public final class CallExpression implements Expression {

    private final Expression callee;
    private final List<Expression> arguments;

    public CallExpression(final Expression callee, final List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value fnValue = callee.evaluate(env);

        if (!(fnValue.asJavaObject() instanceof BotifyFunction))
            throw new RuntimeException("Can only call functions/tasks.");

        final BotifyFunction function = (BotifyFunction) fnValue.asJavaObject();
        final List<Value> args = new ArrayList<>();
        for (final Expression expr : arguments)
            args.add(expr.evaluate(env));

        return function.call(args);
    }
}