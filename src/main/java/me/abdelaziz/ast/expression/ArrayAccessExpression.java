package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import java.util.List;

public final class ArrayAccessExpression implements Expression {

    private final Expression object, index;

    public ArrayAccessExpression(final Expression object, final Expression index) {
        this.object = object;
        this.index = index;
    }

    @Override
    public Value evaluate(final Environment env) {
        final Value objVal = object.evaluate(env),
                idxVal = index.evaluate(env);

        if (!(objVal.asJavaObject() instanceof List))
            throw new RuntimeException("Cannot access index of non-list.");

        @SuppressWarnings("unchecked") final List<Value> list = (List<Value>) objVal.asJavaObject();
        final int i = idxVal.asInt();

        if (i < 0 || i >= list.size())
            throw new RuntimeException("Index out of bounds: " + i);

        return list.get(i);
    }
}