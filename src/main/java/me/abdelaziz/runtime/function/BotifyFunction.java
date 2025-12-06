package me.abdelaziz.runtime.function;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.exception.ReturnException;

import java.util.List;

public final class BotifyFunction implements BotifyCallable {

    private final List<String> parameters;
    private final List<Statement> body;
    private final Environment closure;

    public BotifyFunction(final List<String> parameters, final List<Statement> body, final Environment closure) {
        this.parameters = parameters;
        this.body = body;
        this.closure = closure;
    }

    public Value call(final Environment ignoredEnv, final List<Value> arguments) {
        final Environment fnEnv = new Environment(closure);

        if (arguments.size() != parameters.size())
            throw new RuntimeException("Expected " + parameters.size() + " args, got " + arguments.size());

        for (int i = 0; i < parameters.size(); i++)
            fnEnv.define(parameters.get(i), arguments.get(i), false);

        try {
            for (final Statement stmt : body)
                stmt.execute(fnEnv);
        } catch (final ReturnException returnValue) {
            return returnValue.value;
        }

        return new Value(null);
    }

    @Override
    public String toString() {
        return "<task>";
    }
}