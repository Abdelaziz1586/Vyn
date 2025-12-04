package me.abdelaziz.ast;

import me.abdelaziz.runtime.Environment;

public interface Statement extends Node {
    void execute(final Environment env);
}