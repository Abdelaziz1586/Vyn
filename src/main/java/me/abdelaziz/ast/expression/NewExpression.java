package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import java.util.List;

public final class NewExpression implements Expression {

    private final String className;

    public NewExpression(final String className) {
        this.className = className;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value classDef = env.get(className);

        @SuppressWarnings("unchecked") final List<Statement> body = (List<Statement>) classDef.asJavaObject();

        final Environment instanceEnv = new Environment(env);
        for (final Statement stmt : body) stmt.execute(instanceEnv);
        return new Value(new BotifyInstance(instanceEnv));
    }
}