package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.List;

public final class MinMaxNativeFunction extends NativeFunction {

    public MinMaxNativeFunction(final boolean isMin) {
        super((env, args) -> {
            if (args.size() < 2)
                throw new RuntimeException("Function '" + (isMin ? "min" : "max") + "' requires at least 2 arguments.");

            return getValue(args, isMin);
        });
    }

    private static Value getValue(final List<Value> args, final boolean isMin) {
        Value result = args.get(0);
        double resultValue = result.asDouble();

        for (int i = 1; i < args.size(); i++) {
            final Value current = args.get(i);
            final double currentValue = current.asDouble();

            if (isMin ? currentValue < resultValue : currentValue > resultValue) {
                result = current;
                resultValue = currentValue;
            }
        }

        return result;
    }
}
