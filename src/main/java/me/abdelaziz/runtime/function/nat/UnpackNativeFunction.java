package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.util.SimpleJson;

public final class UnpackNativeFunction extends NativeFunction {

    public UnpackNativeFunction() {
        super((env, args) -> SimpleJson.unpack(args.get(0).toString()));
    }
}
