package me.abdelaziz.runtime.function;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

import java.util.List;

public interface VynCallable {
    Value call(Environment env, List<Value> arguments);
}