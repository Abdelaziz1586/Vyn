package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.BotifyFunction;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.List;

public final class FunctionDeclaration implements Statement {

    private final String name;
    private final List<String> params;
    private final List<Statement> body;

    public FunctionDeclaration(final String name, final List<String> params, final List<Statement> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public void execute(final Environment env) {
        final BotifyFunction function = new BotifyFunction(params, body, env);
        env.define(name, new Value(function), true);
    }
}