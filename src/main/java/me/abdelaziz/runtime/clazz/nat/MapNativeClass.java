package me.abdelaziz.runtime.clazz.nat;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapNativeClass extends NativeClass {

    public MapNativeClass() {
        super(env -> {
            final Map<Value, Value> map = new HashMap<>();

            env.defineFunction("put", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("put() requires 2 arguments");

                map.put(args.get(0), args.get(1));
                return null;
            }));

            env.defineFunction("containsKey", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("containsKey() requires at least 1 argument");

                boolean b = false;
                for (final Value value : args) {
                    b = map.containsKey(value);
                    if (!b)
                        break;
                }

                return new Value(b);
            }));

            env.defineFunction("containsValue", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("containsValue() requires at least 1 argument");

                boolean b = false;
                for (final Value value : args) {
                    b = map.containsValue(value);
                    if (!b)
                        break;
                }

                return new Value(b);
            }));

            env.defineFunction("containsAnyKey", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("containsAnyKey() requires at least 1 argument");

                for (final Value value : args)
                    if (map.containsKey(value))
                        return new Value(true);

                return new Value(false);
            }));

            env.defineFunction("containsAnyValue", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("containsAnyValue() requires at least 1 argument");

                for (final Value value : args)
                    if (map.containsValue(value))
                        return new Value(true);

                return new Value(false);
            }));

            env.defineFunction("remove", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("remove() requires at least 1 argument");

                final List<Value> removedList = new ArrayList<>();
                for (final Value arg : args) {
                    final Value removed = map.remove(arg);
                    if (removed != null)
                        removedList.add(removed);
                }

                return new Value(removedList);
            }));

            env.defineFunction("get", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("get() requires at least 1 argument");

                if (map.isEmpty())
                    return new Value(null);

                final List<Value> values = new ArrayList<>();
                for (final Value arg : args) {
                    final Value value = map.get(arg);
                    if (value != null)
                        values.add(value);
                }

                return new Value(values.isEmpty() ? null : values.size() == 1 ? values.get(0) : values);
            }));

            env.defineFunction("size", new NativeFunction((callerEnv, args) -> new Value(map.size())));

            env.defineFunction("isEmpty", new NativeFunction((callerEnv, args) -> new Value(map.isEmpty())));

            env.defineFunction("stringify", new NativeFunction((callerEnv, args) -> new Value(map.toString())));

        });
    }
}
