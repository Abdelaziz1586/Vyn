package me.abdelaziz.api;

import me.abdelaziz.runtime.Environment;

public interface BotifyLibrary {
    void onEnable(final Environment env);
    void onDisable();
}