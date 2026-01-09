package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;

public final class PrintStatement implements Statement {

    private final boolean isNewLine;
    private final Expression expression;

    public PrintStatement(final Expression expression, final boolean isNewLine) {
        this.expression = expression;
        this.isNewLine = isNewLine;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isNewLine() {
        return isNewLine;
    }

    @Override
    public void execute(final Environment env) {
        final Object val = expression.evaluate(env);
        if (isNewLine) {
            System.out.println(val);
            return;
        }

        System.out.print(val);
    }
}