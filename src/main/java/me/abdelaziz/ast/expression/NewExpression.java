package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.BotifyClass;
import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class NewExpression implements Expression {

    private final String className;

    public NewExpression(final String className) {
        this.className = className;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value classVal = env.get(className);

        if (!(classVal.asJavaObject() instanceof BotifyClass))
            throw new RuntimeException(className + " is not a blueprint.");

        final BotifyClass botifyClass = (BotifyClass) classVal.asJavaObject();
        final Environment instanceEnv = new Environment(botifyClass.getClosure());

        for (final Statement stmt : botifyClass.getBody())
            stmt.execute(instanceEnv);

        return new Value(new BotifyInstance(instanceEnv));
    }
}