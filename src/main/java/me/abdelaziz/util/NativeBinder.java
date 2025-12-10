package me.abdelaziz.util;

import me.abdelaziz.api.annotation.BotifyConstructor;
import me.abdelaziz.api.annotation.BotifyDestructor;
import me.abdelaziz.api.annotation.BotifyFunc;
import me.abdelaziz.api.annotation.BotifyType;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.BotifyClass;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public final class NativeBinder {

    public static void bind(final Environment env, final Class<?> javaClass) {
        if (!javaClass.isAnnotationPresent(BotifyType.class))
            throw new RuntimeException("Class " + javaClass.getName() + " missing @BotifyType.");

        final BotifyType typeAnno = javaClass.getAnnotation(BotifyType.class);
        final String className = typeAnno.name();
        final String parentName = typeAnno.mimics().isEmpty() ? null : typeAnno.mimics();

        final Statement classBody = (instanceEnv) -> {
            for (final Constructor<?> ctor : javaClass.getConstructors()) {
                if (ctor.isAnnotationPresent(BotifyConstructor.class)) {
                    final String initName = "_init_" + ctor.getParameterCount();
                    instanceEnv.defineFunction(initName, new NativeFunction((callerEnv, args) -> {
                        try {
                            final Object[] javaArgs = convertArgs(args, ctor.getParameterTypes());
                            final Object javaInstance = ctor.newInstance(javaArgs);
                            instanceEnv.define("__host__", new Value(javaInstance), true);
                            return new Value(null);
                        } catch (final InvocationTargetException e) {
                            throw unwrap(e);
                        } catch (final Exception e) {
                            throw new RuntimeException("Constructor Error: " + e.getMessage());
                        }
                    }));
                }
            }

            for (final Method method : javaClass.getMethods()) {
                if (method.isAnnotationPresent(BotifyDestructor.class)) {
                    instanceEnv.defineFunction("_destroy", new NativeFunction((callerEnv, args) -> {
                        if (!instanceEnv.has("__host__")) return new Value(null);
                        final Object javaHost = instanceEnv.get("__host__").asJavaObject();
                        try {
                            method.invoke(javaHost);
                            return new Value(null);
                        } catch (final InvocationTargetException e) {
                            throw unwrap(e);
                        } catch (final Exception e) {
                            throw new RuntimeException("Destructor Error: " + e.getMessage());
                        }
                    }));
                }

                if (method.isAnnotationPresent(BotifyFunc.class)) {
                    final BotifyFunc funcAnno = method.getAnnotation(BotifyFunc.class);
                    final String funcName = funcAnno.name().isEmpty() ? method.getName() : funcAnno.name();
                    instanceEnv.defineFunction(funcName, new NativeFunction((callerEnv, args) -> {
                        if (!instanceEnv.has("__host__"))
                            throw new RuntimeException("Native object not initialized.");
                        final Object javaHost = instanceEnv.get("__host__").asJavaObject();
                        try {
                            final Object[] javaArgs = convertArgs(args, method.getParameterTypes());
                            final Object result = method.invoke(javaHost, javaArgs);
                            return toValue(result);
                        } catch (final InvocationTargetException e) {
                            throw unwrap(e);
                        } catch (final Exception e) {
                            throw new RuntimeException("Method Error: " + e.getMessage());
                        }
                    }));
                }
            }
        };

        env.define(className, new Value(new BotifyClass(className, parentName, Collections.singletonList(classBody), env)), true);
    }

    private static RuntimeException unwrap(final InvocationTargetException e) {
        final Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) return (RuntimeException) cause;
        return new RuntimeException(cause != null ? cause.getMessage() : "Unknown Native Error");
    }

    private static Object[] convertArgs(final List<Value> args, final Class<?>[] types) {
        if (args.size() != types.length)
            throw new RuntimeException("Argument mismatch. Expected " + types.length + ", got " + args.size());
        final Object[] javaArgs = new Object[types.length];
        for (int i = 0; i < types.length; i++)
            javaArgs[i] = convert(args.get(i), types[i]);
        return javaArgs;
    }

    private static Object convert(final Value val, final Class<?> target) {
        if (target == String.class) return val.toString();
        if (target == int.class || target == Integer.class) return val.asInt();
        if (target == double.class || target == Double.class) return val.asDouble();
        if (target == boolean.class || target == Boolean.class) return val.asBoolean();
        return val.asJavaObject();
    }

    private static Value toValue(final Object obj) {
        if (obj == null) return new Value(null);
        if (obj instanceof Value) return (Value) obj;
        return new Value(obj);
    }
}