package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class PropertySetStatement implements Statement {

    private final String propertyName;
    private final Expression value, object;

    public PropertySetStatement(final Expression object, final String propertyName, final Expression value) {
        this.object = object; this.propertyName = propertyName; this.value = value;
    }

    public Expression getObject() {
        return object;
    }

    public Expression getValue() {
        return value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public void execute(final Environment env) {
        final Value objVal = object.evaluate(env);
        if (!(objVal.asJavaObject() instanceof VynInstance))
            throw new RuntimeException("Cannot set property on a non-object.");

        ((VynInstance) objVal.asJavaObject()).set(propertyName, value.evaluate(env));
    }
}