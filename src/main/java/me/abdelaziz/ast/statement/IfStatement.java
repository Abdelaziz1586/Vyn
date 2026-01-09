package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import java.util.List;

public final class IfStatement implements Statement {

    private final Expression condition;
    private final List<Statement> thenBranch, elseBranch;

    public IfStatement(final Expression condition, final List<Statement> thenBranch, final List<Statement> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getThenBranch() {
        return thenBranch;
    }

    public List<Statement> getElseBranch() {
        return elseBranch;
    }

    @Override
    public void execute(final Environment env) {
        if (condition.evaluate(env).asBoolean())
            for (final Statement stmt : thenBranch)
                stmt.execute(env);
        else if (elseBranch != null)
            for (final Statement stmt : elseBranch)
                stmt.execute(env);
    }
}