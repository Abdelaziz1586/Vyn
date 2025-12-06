package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Value;

import java.util.List;
import java.util.Map;

public final class SizeNativeFunction extends NativeFunction {

    public SizeNativeFunction() {
        super((env, args) -> {
            if (args.isEmpty())
                throw new RuntimeException("Function 'size' requires 1 argument.");

            final Object obj = args.get(0).asJavaObject();

            if (obj instanceof List)
                return new Value(((List<?>) obj).size());

            if (obj instanceof String)
                return new Value(((String) obj).length());

            if (obj instanceof Map)
                return new Value(((Map<?, ?>) obj).size());
            
            return new Value(0);
        });
    }
}