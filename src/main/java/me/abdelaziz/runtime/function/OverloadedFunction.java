package me.abdelaziz.runtime.function;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OverloadedFunction implements VynCallable {

    private final Map<Integer, VynCallable> functions = new HashMap<>();

    public void addFunction(final VynCallable function) {
        functions.put(function.arity(), function);
    }

    @Override
    public Value call(final Environment env, final List<Value> arguments) {
        final int arity = arguments.size();

        if (functions.containsKey(arity))
            return functions.get(arity).call(env, arguments);

        if (functions.containsKey(-1))
            return functions.get(-1).call(env, arguments);

        throw new RuntimeException("No function overload found for " + arity + " arguments.");
    }

    @Override
    public String toString() {
        return "<overloaded function>";
    }
}
