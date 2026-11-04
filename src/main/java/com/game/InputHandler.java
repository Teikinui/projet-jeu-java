
package com.game;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {

    private Set<KeyCode> touches = new HashSet<>();

    public void pollEvents(Scene scene) {

        scene.setOnKeyPressed(event -> {
            touches.add(event.getCode());
            System.out.println("Touche pressée : " + event.getCode());
        });

        scene.setOnKeyReleased(event -> {
            touches.remove(event.getCode());
        });
    }

    public boolean isPressed(KeyCode key) {
        return touches.contains(key);
    }
}