package me.abdelaziz.api;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;

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
       NativeBinder.bind(env, javaClass);
    }
}