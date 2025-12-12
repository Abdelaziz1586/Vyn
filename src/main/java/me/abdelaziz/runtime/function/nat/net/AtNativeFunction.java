package me.abdelaziz.runtime.function.nat.net;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.List;

public final class AtNativeFunction extends NativeFunction {

    @SuppressWarnings("rawtypes")
    public AtNativeFunction() {
        super((env, args) -> (Value) ((List) args.get(0).asJavaObject()).get(args.get(1).asInt()));
    }

}
