package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;

import java.util.List;

public final class SplitStatement implements Statement {

    private final List<Statement> body;

    public SplitStatement(final List<Statement> body) {
        this.body = body;
    }

    @Override
    public void execute(final Environment env) {
        final Environment threadEnv = new Environment(env);

        final Thread thread = new Thread(() -> {
            try {
                for (final Statement stmt : body) {
                    stmt.execute(threadEnv);
                }
            } catch (final Exception e) {
                System.err.println("Error in split execution: " + e.getMessage());
            }
        });

        thread.setDaemon(false);
        thread.start();
    }
}