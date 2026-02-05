package me.abdelaziz.runtime.function.nat.math;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

public final class RandomNativeFunction extends NativeFunction {

    public RandomNativeFunction() {
        super((env, args) -> {
            switch (args.size()) {
                case 0:
                    return new Value(Math.random());
                case 1:
                    return new Value((int) (Math.random() * (args.get(0).asDouble() + 1)));
                case 2:
                    final int min = args.get(0).asDouble().intValue(),
                            max = args.get(1).asDouble().intValue();

                    return new Value(min + (int) (Math.random() * (max - min + 1)));
            }

            throw new RuntimeException("Usage: random(), random(max), or random(min, max)");
        });
    }

}
