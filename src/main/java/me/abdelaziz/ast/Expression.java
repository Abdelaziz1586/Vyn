package me.abdelaziz.ast;

import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;

public interface Expression extends Node {
    Value evaluate(final Environment env);
}