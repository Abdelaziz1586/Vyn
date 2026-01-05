package me.abdelaziz.runtime.clazz.nat;

import me.abdelaziz.runtime.Environment;
import java.util.function.Consumer;

public abstract class NativeClass {

    private final Consumer<Environment> logic;

    public NativeClass(final Consumer<Environment> logic) {
        this.logic = logic;
    }

    public void execute(final Environment env) {
        logic.accept(env);
    }
}
