package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.expression.GetExpression;
import me.abdelaziz.ast.expression.VariableExpression;
import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.runtime.function.VynCallable;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.ArrayList;
import java.util.List;

public final class CallExpression implements Expression {

    private final Expression callee;
    private final List<Expression> arguments;

    public CallExpression(final Expression callee, final List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public Value evaluate(final Environment env) {
        final VynCallable function;

        if (callee instanceof VariableExpression) {
            function = env.getFunction(((VariableExpression) callee).getName());
        } else if (callee instanceof GetExpression) {
            final GetExpression getExpr = (GetExpression) callee;
            final Value objectValue = getExpr.getObject().evaluate(env);

            if (!(objectValue.asJavaObject() instanceof VynInstance))
                throw new RuntimeException("Cannot call method on non-object.");

            function = ((VynInstance) objectValue.asJavaObject()).getMethod(getExpr.getName());
        } else {
            throw new RuntimeException("Invalid function call target.");
        }

        final List<Value> args = new ArrayList<>();
        for (final Expression expr : arguments)
            args.add(expr.evaluate(env));

        return function.call(env, args);
    }
}