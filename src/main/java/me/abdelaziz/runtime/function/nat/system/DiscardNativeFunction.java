package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.Collections;

public final class DiscardNativeFunction extends NativeFunction {

    public DiscardNativeFunction() {
        super((env, args) -> {
            if (args.isEmpty()) return new Value(null);

            final Object obj = args.get(0).asJavaObject();
            if (obj instanceof VynInstance) {
                final VynInstance inst = (VynInstance) obj;

                if (inst.has("_destroy"))
                    inst.getMethod("_destroy").call(env, Collections.emptyList());
            }
            return new Value(null);
        });
    }
}