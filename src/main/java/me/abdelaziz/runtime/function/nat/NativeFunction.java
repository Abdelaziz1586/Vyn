package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.VynCallable;

import java.util.List;
import java.util.function.BiFunction;

public class NativeFunction implements VynCallable {

    private final int arity;
    private final BiFunction<Environment, List<Value>, Value> logic;

    public NativeFunction(final int arity, final BiFunction<Environment, List<Value>, Value> logic) {
        this.arity = arity;
        this.logic = logic;
    }

    public NativeFunction(final BiFunction<Environment, List<Value>, Value> logic) {
        this(-1, logic);
    }

    @Override
    public int arity() {
        return arity;
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