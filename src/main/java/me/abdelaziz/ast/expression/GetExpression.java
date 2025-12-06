package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.Map;

public final class GetExpression implements Expression {

    private final String name;
    private final Expression object;

    public GetExpression(final Expression object, final String name) {
        this.object = object;
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Value evaluate(final Environment env) {
        final Value obj = object.evaluate(env);
        final Object raw = obj.asJavaObject();

        if (raw instanceof BotifyInstance)
            return ((BotifyInstance) raw).get(name);

        if (raw instanceof Map)
            return ((Map<String, Value>) raw).getOrDefault(name, new Value(null));

        throw new RuntimeException("Cannot access property '" + name + "' on " + obj);
    }
}