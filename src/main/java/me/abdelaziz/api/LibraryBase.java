package me.abdelaziz.api;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynDestructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.runtime.VynClass;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public abstract class LibraryBase implements VynLibrary {

    @Override
    public void onDisable() {
    }

    protected void bind(final Environment env, final String name,
            final BiFunction<Environment, List<Value>, Value> logic) {
        env.defineFunction(name, new NativeFunction(logic));
    }

    protected void defineVariable(final Environment env, final String name, final Object javaInstance) {
        NativeBinder.defineVariable(env, name, javaInstance);
    }

    protected void defineConstant(final Environment env, final String name, final Object javaInstance) {
        NativeBinder.defineConstant(env, name, javaInstance);
    }

    protected void registerClass(final Environment env, final Class<?> javaClass) {
        if (!javaClass.isAnnotationPresent(VynType.class))
            throw new RuntimeException("Class " + javaClass.getName() + " missing @VynType annotation");

        final VynType typeAnno = javaClass.getAnnotation(VynType.class);
        final String className = typeAnno.name();
        final String parentName = typeAnno.mimics().isEmpty() ? null : typeAnno.mimics();

        final Statement classBody = (instanceEnv) -> {
            for (final Constructor<?> ctor : javaClass.getConstructors()) {
                if (ctor.isAnnotationPresent(VynConstructor.class)) {
                    final int paramCount = ctor.getParameterCount();
                    final String initName = "_init_" + paramCount;

                    instanceEnv.defineFunction(initName, new NativeFunction((callerEnv, args) -> {
                        try {
                            instanceEnv.define("__host__",
                                    new Value(ctor.newInstance(convertArgs(args, ctor.getParameterTypes()))), true);
                            return new Value(null);
                        } catch (final Exception e) {
                            throw new RuntimeException("Error in native constructor: " + getErrorMsg(e));
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
                        } catch (final Exception e) {
                            throw new RuntimeException("Destructor Error: " + getErrorMsg(e));
                        }
                    }));
                }

                if (method.isAnnotationPresent(VynFunc.class)) {
                    final VynFunc funcAnno = method.getAnnotation(VynFunc.class);
                    final String funcName = funcAnno.name().isEmpty() ? method.getName() : funcAnno.name();

                    instanceEnv.defineFunction(funcName, new NativeFunction((callerEnv, args) -> {
                        if (!instanceEnv.has("__host__"))
                            throw new RuntimeException("Native object not initialized.");

                        final Object javaHost = instanceEnv.get("__host__").asJavaObject();
                        try {
                            return toValue(method.invoke(javaHost, convertArgs(args, method.getParameterTypes())));
                        } catch (final Exception e) {
                            throw new RuntimeException("Error in native method '" + funcName + "': " + getErrorMsg(e));
                        }
                    }));
                }
            }
        };

        env.define(className,
                new Value(new VynClass(className, parentName, Collections.singletonList(classBody), env)), true);
    }

    private String getErrorMsg(final Exception e) {
        if (e instanceof InvocationTargetException) {
            final Throwable cause = e.getCause();
            return cause != null ? cause.getMessage() : e.getMessage();
        }
        return e.getMessage();
    }

    private Object[] convertArgs(final List<Value> args, final Class<?>[] types) {
        if (args.size() != types.length)
            throw new RuntimeException("Argument mismatch. Expected " + types.length + ", got " + args.size());

        final Object[] javaArgs = new Object[types.length];
        for (int i = 0; i < types.length; i++)
            javaArgs[i] = convert(args.get(i), types[i]);

        return javaArgs;
    }

    private Object convert(final Value val, final Class<?> target) {
        if (target == String.class)
            return val.toString();
        if (target == int.class || target == Integer.class)
            return val.asInt();
        if (target == double.class || target == Double.class)
            return val.asDouble();
        if (target == boolean.class || target == Boolean.class)
            return val.asBoolean();

        return val.asJavaObject();
    }

    private Value toValue(final Object obj) {
        if (obj == null)
            return new Value(null);
        if (obj instanceof Value)
            return (Value) obj;
        return new Value(obj);
    }
}