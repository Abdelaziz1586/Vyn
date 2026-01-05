package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.List;

public final class MaxNativeFunction extends NativeFunction {

    public MaxNativeFunction() {
        super((env, args) -> {
            if (args.size() < 2)
                throw new RuntimeException("Function 'max' requires at least 2 arguments.");

            return getMaxValue(args);
        });
    }

    private static Value getMaxValue(final List<Value> args) {
        Value max = args.get(0);
        double maxValue = max.asDouble();

        for (int i = 1; i < args.size(); i++) {
            final Value current = args.get(i);
            final double currentValue = current.asDouble();

            if (currentValue > maxValue) {
                max = current;
                maxValue = currentValue;
            }
        }

        return max;
    }
}
