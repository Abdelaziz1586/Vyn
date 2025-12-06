package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.util.SimpleJson;

public final class PackNativeFunction extends NativeFunction {

    public PackNativeFunction() {
        super((env, args) -> new Value(SimpleJson.pack(args.get(0).asJavaObject())));
    }

}
