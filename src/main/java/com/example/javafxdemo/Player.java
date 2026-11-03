package com.example.javafxdemo;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

public class Player extends Entity {
    private final double speed;
    private final GameEventHandler eventHandler;

    public Player(String spritePath, Point2D position, double speed, GameEventHandler eventHandler) {
        super(spritePath, position);
        this.speed = speed;
        this.eventHandler = eventHandler;

        sprite.setFitWidth(64);
        sprite.setFitHeight(64);
        sprite.setPreserveRatio(true);
    }

    @Override
    public void update() {
        double dx = 0;
        double dy = 0;

        if (eventHandler.isPressed(KeyCode.LEFT)) {
            dx -= 1;
        }
        if (eventHandler.isPressed(KeyCode.RIGHT)) {
            dx += 1;
        }
        if (eventHandler.isPressed(KeyCode.UP)) {
            dy -= 1;
        }
        if (eventHandler.isPressed(KeyCode.DOWN)) {
            dy += 1;
        }

        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        setPosition(getPosition().add(dx * speed, dy * speed));
    }
}
