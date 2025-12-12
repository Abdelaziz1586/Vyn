package me.abdelaziz.runtime.function.nat.conversion;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

public final class StringNativeFunction extends NativeFunction {

    public StringNativeFunction() {
        super((env, args) -> {
            if (args.isEmpty())
                throw new RuntimeException("Function 'string' requires 1 argument.");

            return new Value(args.get(0).toString());
        });
    }
}
