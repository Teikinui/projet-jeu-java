package com.teikinui.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.teikinui.game.TripTrapGame;

/** Lanceur de la version ordinateur (Windows, Linux, macOS). */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TripTrap");
        config.setWindowedMode(960, 540);
        config.useVsync(true);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new TripTrapGame(), config);
    }
}
