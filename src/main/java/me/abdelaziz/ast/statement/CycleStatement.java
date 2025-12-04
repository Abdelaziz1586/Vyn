package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.List;

public final class CycleStatement implements Statement {

    private final String variableName;
    private final Expression startExpr, endExpr;
    private final Expression conditionExpr;
    private final List<Statement> body;

    public CycleStatement(final String variableName, final Expression start, final Expression end, final List<Statement> body) {
        this.variableName = variableName;
        this.startExpr = start;
        this.endExpr = end;
        this.conditionExpr = null;
        this.body = body;
    }

    public CycleStatement(final Expression condition, final List<Statement> body) {
        this.variableName = null;
        this.startExpr = null;
        this.endExpr = null;
        this.conditionExpr = condition;
        this.body = body;
    }

    @Override
    public void execute(final Environment env) {
        if (conditionExpr != null) {
            while (conditionExpr.evaluate(env).asBoolean())
                executeBody(env);
            return;
        }

        if (startExpr == null || endExpr == null) return;

        final double start = startExpr.evaluate(env).asDouble();
        final double end = endExpr.evaluate(env).asDouble();

        for (double i = start; i <= end; i++) {
            final Environment loopEnv = new Environment(env);
            loopEnv.define(variableName, new Value(i), false);

            for (final Statement stmt : body)
                stmt.execute(loopEnv);
        }
    }

    private void executeBody(final Environment parentEnv) {
        final Environment loopEnv = new Environment(parentEnv);
        for (final Statement stmt : body)
            stmt.execute(loopEnv);
    }
}