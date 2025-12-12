package me.abdelaziz.runtime.function.nat.net;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.SimpleJson;

public final class UnpackNativeFunction extends NativeFunction {

    public UnpackNativeFunction() {
        super((env, args) -> SimpleJson.unpack(args.get(0).toString()));
    }
}
