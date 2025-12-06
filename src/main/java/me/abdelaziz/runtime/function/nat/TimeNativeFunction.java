package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Value;

public final class TimeNativeFunction extends NativeFunction {

    public TimeNativeFunction() {
        super((env, args) -> new Value((double) System.currentTimeMillis()));
    }
}
