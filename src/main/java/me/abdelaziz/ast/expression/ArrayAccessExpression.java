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
    @SuppressWarnings("unchecked")
    public Value evaluate(final Environment env) {
        final Value objVal = object.evaluate(env),
                idxVal = index.evaluate(env);

        if (objVal.asJavaObject() instanceof List)
            return getIndexFromList((List<Value>) objVal.asJavaObject(), idxVal.asInt());

        if (objVal.asJavaObject() instanceof String)
            return getIndexFromString((String) objVal.asJavaObject(), idxVal.asInt());

        throw new RuntimeException("Cannot access index of non-list or non-string.");
    }

    private Value getIndexFromList(final List<Value> list, final int i) {
        if (i < 0 || i >= list.size())
            throw new RuntimeException("Index out of bounds: " + i);

        return list.get(i);
    }

    private Value getIndexFromString(final String s, final int i) {
        if (i < 0 || i >= s.length())
            throw new RuntimeException("Index out of bounds: " + i);

        return new Value(String.valueOf(s.charAt(i)));
    }
}