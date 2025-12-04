package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import java.util.List;

public final class IfStatement implements Statement {

    private final Expression condition;
    private final List<Statement> body;

    public IfStatement(final Expression condition, final List<Statement> body) {
        this.condition = condition; this.body = body;
    }

    @Override
    public void execute(final Environment env) {
        if (condition.evaluate(env).asBoolean())
            for (final Statement stmt : body)
                stmt.execute(env);
    }
}