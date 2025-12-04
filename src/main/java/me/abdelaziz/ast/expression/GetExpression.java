package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class GetExpression implements Expression {

    private final String name;
    private final Expression object;

    public GetExpression(final Expression object, final String name) {
        this.object = object;
        this.name = name;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value obj = object.evaluate(env);
        if (obj.asJavaObject() instanceof BotifyInstance)
            return ((BotifyInstance) obj.asJavaObject()).get(name);

        throw new RuntimeException("Only instances have properties.");
    }
}