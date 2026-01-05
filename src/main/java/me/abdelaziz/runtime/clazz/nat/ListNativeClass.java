package me.abdelaziz.runtime.clazz.nat;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.ArrayList;
import java.util.List;

public final class ListNativeClass extends NativeClass {

    public ListNativeClass() {
        super(env -> {
            final List<Value> list = new ArrayList<>();

            env.defineFunction("add", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("add() requires at least 1 argument");

                list.addAll(args);
                return null;
            }));

            env.defineFunction("contains", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("contains() requires at least 1 argument");

                boolean b = false;
                for (final Value value : args) {
                    b = list.contains(value);
                    if (!b)
                        break;
                }

                return new Value(b);
            }));

            env.defineFunction("containsAny", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("containsAny() requires at least 1 argument");

                for (final Value value : args)
                    if (list.contains(value))
                        return new Value(true);

                return new Value(false);
            }));

            env.defineFunction("remove", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("remove() requires at least 1 argument");

                list.removeAll(args);
                return null;
            }));

            env.defineFunction("get", new NativeFunction((callerEnv, args) -> {
                if (args.isEmpty())
                    throw new RuntimeException("get() requires at least 1 argument");

                if (list.isEmpty())
                    return new Value(null);

                final List<Value> values = new ArrayList<>();
                for (final Value arg : args) {
                    final int intVal = arg.asInt();
                    if (intVal < 0 || intVal >= list.size())
                        continue;

                    values.add(list.get(intVal));
                }

                return new Value(values.isEmpty() ? null : values.size() == 1 ? values.get(0) : values);
            }));

            env.defineFunction("size", new NativeFunction((callerEnv, args) -> new Value(list.size())));

            env.defineFunction("isEmpty", new NativeFunction((callerEnv, args) -> new Value(list.isEmpty())));

            env.defineFunction("stringify", new NativeFunction((callerEnv, args) -> new Value(list.toString())));
        });
    }

    @Override
    public String toString() {
        return "";
    }
}
