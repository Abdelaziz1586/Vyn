package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.List;

public final class MinNativeFunction extends NativeFunction {

    public MinNativeFunction() {
        super((env, args) -> {
            if (args.size() < 2)
                throw new RuntimeException("Function 'min' requires at least 2 arguments.");

            return getMinValue(args);
        });
    }

    private static Value getMinValue(final List<Value> args) {
        Value min = args.get(0);
        double minValue = min.asDouble();

        for (int i = 1; i < args.size(); i++) {
            final Value current = args.get(i);
            final double currentValue = current.asDouble();

            if (currentValue < minValue) {
                min = current;
                minValue = currentValue;
            }
        }

        return min;
    }
}
