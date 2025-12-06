package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import java.util.List;

public final class ArraySetStatement implements Statement {

    private final Expression object, index, value;

    public ArraySetStatement(final Expression object, final Expression index, final Expression value) {
        this.object = object;
        this.index = index;
        this.value = value;
    }

    @Override
    public void execute(final Environment env) {
        final Value objVal = object.evaluate(env),
                idxVal = index.evaluate(env),
                valToSet = value.evaluate(env);

        if (!(objVal.asJavaObject() instanceof List))
            throw new RuntimeException("Cannot set index of non-list.");

        @SuppressWarnings("unchecked") final List<Value> list = (List<Value>) objVal.asJavaObject();
        final int i = idxVal.asInt();

        if (i < 0 || i >= list.size())
            throw new RuntimeException("Index out of bounds: " + i);

        list.set(i, valToSet);
    }
}