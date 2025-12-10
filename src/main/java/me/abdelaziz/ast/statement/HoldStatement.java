package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;

public final class HoldStatement implements Statement {

    private final Expression timeExpr;

    public HoldStatement(final Expression timeExpr) {
        this.timeExpr = timeExpr;
    }

    @Override
    public void execute(final Environment env) {
        final long time = timeExpr.evaluate(env).asDouble().longValue();
        try {
            Thread.sleep(time);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Hold interrupted");
        }
    }
}