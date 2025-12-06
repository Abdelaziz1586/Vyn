package me.abdelaziz.api;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public abstract class LibraryBase implements BotifyLibrary {

    protected void bind(final Environment env, final String name, final BiFunction<Environment, List<Value>, Value> logic) {
        env.defineFunction(name, new NativeFunction(logic));
    }

    protected void bindConst(final Environment env, final String name, final Object val) {
        env.define(name, new Value(val), true);
    }
}