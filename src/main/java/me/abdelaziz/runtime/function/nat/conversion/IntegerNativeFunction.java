package me.abdelaziz.runtime.function.nat.conversion;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

public final class IntegerNativeFunction extends NativeFunction {

    public IntegerNativeFunction() {
        super((env, args) -> {
            if (args.isEmpty())
                throw new RuntimeException("Function 'int' requires 1 argument.");

            return new Value(args.get(0).asInt());
        });
    }
}
