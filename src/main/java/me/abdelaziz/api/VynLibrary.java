package me.abdelaziz.api;

import me.abdelaziz.runtime.Environment;

public interface VynLibrary {
    void onEnable(final Environment env);
    void onDisable();
}