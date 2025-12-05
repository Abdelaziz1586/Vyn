package me.abdelaziz.runtime.function;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.List;
import java.util.function.BiFunction;

public final class NativeFunction implements BotifyCallable {

    private final BiFunction<Environment, List<Value>, Value> logic;

    public NativeFunction(final BiFunction<Environment, List<Value>, Value> logic) {
        this.logic = logic;
    }

    @Override
    public Value call(final Environment env, final List<Value> arguments) {
        return logic.apply(env, arguments);
    }

    @Override
    public String toString() {
        return "<native code>";
    }
}