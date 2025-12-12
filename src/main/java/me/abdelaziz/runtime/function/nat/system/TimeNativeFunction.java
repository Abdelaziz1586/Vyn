package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

public final class TimeNativeFunction extends NativeFunction {

    public TimeNativeFunction() {
        super((env, args) -> new Value((double) System.currentTimeMillis()));
    }
}
