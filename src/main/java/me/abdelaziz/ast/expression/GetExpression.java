package me.abdelaziz.ast.expression;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.clazz.VynCatalog;

import java.util.Map;

public final class GetExpression implements Expression {

    private final Expression object;
    private final String name;

    public GetExpression(final Expression object, final String name) {
        this.object = object;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Expression getObject() {
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Value evaluate(final Environment env) {
        final Value value = object.evaluate(env);
        final Object raw = value.asJavaObject();

        if (raw instanceof VynInstance)
            return ((VynInstance) raw).get(name);

        if (raw instanceof VynCatalog)
            return ((VynCatalog) raw).get(name);

        if (raw instanceof Map)
            return ((Map<String, Value>) raw).getOrDefault(name, new Value(null));

        throw new RuntimeException("Cannot access property '" + name + "' on " + value);
    }
}