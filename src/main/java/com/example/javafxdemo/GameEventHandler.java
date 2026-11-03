package com.example.javafxdemo;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;

public class GameEventHandler {
    private static final Set<KeyCode> MOVEMENT_KEYS = Set.of(
            KeyCode.UP,
            KeyCode.DOWN,
            KeyCode.LEFT,
            KeyCode.RIGHT
    );
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public void keyPressed(KeyEvent event) {
        KeyCode key = event.getCode();
        if (MOVEMENT_KEYS.contains(key)) {
            pressedKeys.add(key);
        }
    }

    public void keyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    public boolean isPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }
}
