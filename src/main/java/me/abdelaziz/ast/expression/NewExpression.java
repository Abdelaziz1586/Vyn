package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.BotifyClass;
import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.ArrayList;
import java.util.List;

public final class NewExpression implements Expression {

    private final String className;
    private final List<Expression> arguments;

    public NewExpression(final String className, final List<Expression> arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value classVal = env.get(className);

        if (!(classVal.asJavaObject() instanceof BotifyClass))
            throw new RuntimeException(className + " is not a blueprint.");

        final BotifyClass botifyClass = (BotifyClass) classVal.asJavaObject();

        final Environment instanceEnv = new Environment(botifyClass.getClosure());

        final BotifyInstance instance = new BotifyInstance(instanceEnv);

        instanceEnv.define("me", new Value(instance), true);
        construct(botifyClass, instanceEnv, env);

        final List<Value> args = new ArrayList<>();
        for (final Expression expr : arguments)
            args.add(expr.evaluate(env));

        final String ctorName = "_init_" + args.size();

        if (instanceEnv.has(ctorName)) {
            instanceEnv.getFunction(ctorName).call(instanceEnv, args);
        } else if (!args.isEmpty()) {
            throw new RuntimeException("No constructor found for " + className + " with " + args.size() + " arguments.");
        }

        return new Value(instance);
    }

    private void construct(final BotifyClass currentClass, final Environment instanceEnv, final Environment lookupEnv) {
        if (currentClass.getParentName() != null) {
            final Value parentVal = lookupEnv.get(currentClass.getParentName());
            if (!(parentVal.asJavaObject() instanceof BotifyClass))
                throw new RuntimeException("Parent class '" + currentClass.getParentName() + "' not found.");

            construct((BotifyClass) parentVal.asJavaObject(), instanceEnv, lookupEnv);
        }

        for (final Statement stmt : currentClass.getBody())
            stmt.execute(instanceEnv);
    }
}