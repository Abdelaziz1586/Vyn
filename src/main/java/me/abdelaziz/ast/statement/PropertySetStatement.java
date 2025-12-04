package me.abdelaziz.ast.statement;

import me.abdelaziz.ast.Expression;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public final class PropertySetStatement implements Statement {

    private final String propertyName;
    private final Expression value, object;

    public PropertySetStatement(final Expression object, final String propertyName, final Expression value) {
        this.object = object; this.propertyName = propertyName; this.value = value;
    }

    @Override
    public void execute(final Environment env) {
        final Value objVal = object.evaluate(env);
        if (!(objVal.asJavaObject() instanceof BotifyInstance))
            throw new RuntimeException("Cannot set property on a non-object.");

        ((BotifyInstance) objVal.asJavaObject()).set(propertyName, value.evaluate(env));
    }
}