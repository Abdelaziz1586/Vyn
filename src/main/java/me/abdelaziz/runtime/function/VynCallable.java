package me.abdelaziz.runtime.function;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.List;

public interface VynCallable {
    default int arity() {
        return -1;
    }

    Value call(Environment env, List<Value> arguments);
}