package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Value;

import java.util.List;

public final class AtNativeFunction extends NativeFunction {

    @SuppressWarnings("rawtypes")
    public AtNativeFunction() {
        super((env, args) -> (Value) ((List) args.get(0).asJavaObject()).get(args.get(1).asInt()));
    }

}
