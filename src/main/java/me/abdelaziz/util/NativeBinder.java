package me.abdelaziz.util;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynDestructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.clazz.VynClass;
import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NativeBinder {

    public static void defineVariable(final Environment env, final String name, final Object javaInstance) {
        env.define(name, toValue(env, javaInstance), false);
    }

    public static void defineConstant(final Environment env, final String name, final Object javaInstance) {
        env.define(name, toValue(env, javaInstance), true);
    }

    public static Value toValue(final Environment env, final Object javaInstance) {
        if (javaInstance == null)
            return new Value(null);

        if (javaInstance instanceof Value)
            return (Value) javaInstance;

        if (javaInstance instanceof List) {
            final List<?> list = (List<?>) javaInstance;
            final List<Value> values = new ArrayList<>(list.size());
            for (final Object obj : list)
                values.add(toValue(env, obj));

            return new Value(values);
        }

        if (javaInstance instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>) javaInstance;
            final Map<Object, Value> values = new LinkedHashMap<>(map.size());
            for (final Map.Entry<?, ?> entry : map.entrySet())
                values.put(entry.getKey(), toValue(env, entry.getValue()));
            return new Value(values);
        }

        if (javaInstance.getClass().isArray()) {
            final int length = Array.getLength(javaInstance);
            final List<Value> values = new ArrayList<>(length);
            for (int i = 0; i < length; i++)
                values.add(toValue(env, Array.get(javaInstance, i)));
            return new Value(values);
        }

        final Class<?> javaClass = javaInstance.getClass();
        if (javaClass.isAnnotationPresent(VynType.class)) {
            final VynType typeAnno = javaClass.getAnnotation(VynType.class);
            final String className = typeAnno.name();

            if (!env.has(className)) {
                bind(env, javaClass);
            }

            final Value classVal = env.get(className);
            if (classVal.asJavaObject() instanceof VynClass) {
                return new Value(VynInstance.fromHost((VynClass) classVal.asJavaObject(), javaInstance));
            }
        }

        return new Value(javaInstance);
    }

    public static void bind(final Environment env, final Class<?> javaClass) {
        if (!javaClass.isAnnotationPresent(VynType.class))
            throw new RuntimeException("Class " + javaClass.getName() + " missing @VynType.");

        final VynType typeAnno = javaClass.getAnnotation(VynType.class);
        final String className = typeAnno.name();
        final String parentName = typeAnno.mimics().isEmpty() ? null : typeAnno.mimics();

        final Statement classBody = (instanceEnv) -> {
            for (final Constructor<?> ctor : javaClass.getConstructors()) {
                if (ctor.isAnnotationPresent(VynConstructor.class)) {
                    final String initName = "_init_" + ctor.getParameterCount();
                    instanceEnv.defineFunction(initName, new NativeFunction((callerEnv, args) -> {
                        try {
                            final Object[] javaArgs = convertArgs(args, ctor.getGenericParameterTypes());
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
                if (method.isAnnotationPresent(VynDestructor.class)) {
                    instanceEnv.defineFunction("_destroy", new NativeFunction((callerEnv, args) -> {
                        if (!instanceEnv.has("__host__"))
                            return new Value(null);
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

                if (method.isAnnotationPresent(VynFunc.class)) {
                    final VynFunc funcAnno = method.getAnnotation(VynFunc.class);
                    final String funcName = funcAnno.name().isEmpty() ? method.getName() : funcAnno.name();
                    final int arity = method.getParameterCount();
                    instanceEnv.defineFunction(funcName, new NativeFunction(arity, (callerEnv, args) -> {
                        if (!instanceEnv.has("__host__"))
                            throw new RuntimeException("Native object not initialized.");
                        final Object javaHost = instanceEnv.get("__host__").asJavaObject();
                        try {
                            final Object[] javaArgs = convertArgs(args, method.getGenericParameterTypes());
                            final Object result = method.invoke(javaHost, javaArgs);
                            return toValue(callerEnv, result);
                        } catch (final InvocationTargetException e) {
                            throw unwrap(e);
                        } catch (final Exception e) {
                            throw new RuntimeException("Method Error: " + e.getMessage());
                        }
                    }));
                }
            }
        };

        env.define(className,
                new Value(new VynClass(className, parentName, Collections.singletonList(classBody), env)), true);
    }

    private static RuntimeException unwrap(final InvocationTargetException e) {
        final Throwable cause = e.getCause();
        if (cause instanceof RuntimeException)
            return (RuntimeException) cause;
        return new RuntimeException(cause != null ? cause.getMessage() : "Unknown Native Error");
    }

    private static Object[] convertArgs(final List<Value> args, final Type[] types) {
        if (args.size() != types.length)
            throw new RuntimeException("Argument mismatch. Expected " + types.length + ", got " + args.size());
        final Object[] javaArgs = new Object[types.length];
        for (int i = 0; i < types.length; i++)
            javaArgs[i] = convert(args.get(i), types[i]);
        return javaArgs;
    }

    private static Object convert(final Value val, final Type type) {
        Class<?> target = null;
        if (type instanceof Class<?>) {
            target = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            target = (Class<?>) ((ParameterizedType) type).getRawType();
        }

        if (target == null)
            return val.asJavaObject();

        if (target == String.class)
            return val.toString();
        if (target == int.class || target == Integer.class)
            return val.asInt();
        if (target == double.class || target == Double.class)
            return val.asDouble();
        if (target == boolean.class || target == Boolean.class)
            return val.asBoolean();
        if (target == Object.class)
            return val.asJavaObject();

        final Object obj = val.asJavaObject();

        if (List.class.isAssignableFrom(target) && obj instanceof List) {
            final List<?> list = (List<?>) obj;
            final List<Object> newList = new ArrayList<>(list.size());
            final Type componentType = type instanceof ParameterizedType
                    ? ((ParameterizedType) type).getActualTypeArguments()[0]
                    : Object.class;

            for (final Object o : list) {
                // If elements are already Values, convert them. If not, wrap and convert
                // (handling Mixed lists potentially)
                final Value v = o instanceof Value ? (Value) o : new Value(o);
                newList.add(convert(v, componentType));
            }
            return newList;
        }

        if (obj instanceof VynInstance) {
            final VynInstance instance = (VynInstance) obj;
            if (instance.has("__host__")) {
                final Object host = instance.get("__host__").asJavaObject();
                if (target.isInstance(host))
                    return host;
            }
        }

        return obj;
    }

}