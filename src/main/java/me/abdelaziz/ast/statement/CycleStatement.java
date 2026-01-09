package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.exception.BreakException;
import me.abdelaziz.runtime.function.exception.ContinueException;

import java.util.List;

public final class CycleStatement implements Statement {

    private final String variableName;
    private final Expression startExpr, endExpr;
    private final Expression conditionExpr;
    private final List<Statement> body;

    public CycleStatement(final String variableName, final Expression start, final Expression end,
                          final List<Statement> body) {
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

    public List<Statement> getBody() {
        return body;
    }

    public Expression getConditionExpr() {
        return conditionExpr;
    }

    public Expression getEndExpr() {
        return endExpr;
    }

    public Expression getStartExpr() {
        return startExpr;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public void execute(final Environment env) {
        if (conditionExpr != null) {
            final Environment loopEnv = new Environment(env);
            while (conditionExpr.evaluate(env).asBoolean()) {
                try {
                    for (final Statement stmt : body)
                        stmt.execute(loopEnv);
                } catch (final ContinueException ignored) {
                } catch (final BreakException ignored) {
                    break;
                }
            }
            return;
        }

        if (startExpr == null || endExpr == null)
            return;

        final Value startVal = startExpr.evaluate(env);
        final Value endVal = endExpr.evaluate(env);

        final Environment loopEnv = new Environment(env);

        if (startVal.isInteger() && endVal.isInteger()) {
            final long start = startVal.asLong();
            final long end = endVal.asLong();
            final Value iterator = new Value(start);
            loopEnv.define(variableName, iterator, false);

            for (long i = start; i <= end; i++) {
                iterator.set(i);
                try {
                    for (final Statement stmt : body)
                        stmt.execute(loopEnv);
                } catch (final ContinueException ignored) {
                } catch (final BreakException ignored) {
                    break;
                }
            }
        } else {
            final double start = startVal.asDouble();
            final double end = endVal.asDouble();
            final Value iterator = new Value(start);
            loopEnv.define(variableName, iterator, false);

            for (double i = start; i <= end; i++) {
                iterator.set(i);
                try {
                    for (final Statement stmt : body)
                        stmt.execute(loopEnv);
                } catch (final ContinueException ignored) {
                } catch (final BreakException ignored) {
                    break;
                }
            }
        }
    }
}