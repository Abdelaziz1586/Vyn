package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.BotifyCallable;

import java.util.List;
import java.util.function.BiFunction;

public class NativeFunction implements BotifyCallable {

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