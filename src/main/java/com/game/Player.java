
package com.game;

import javafx.scene.input.KeyCode;


public class Player extends Entity {

    private static final double GRAVITY = 0;
    private static final double JUMP_SPEED = 600;

    private double speed;
    private double vx;
    private double vy;
    private boolean onGround;
    private InputHandler inputHandler;

    public Player(double x, double y, double width, double height, double speed,
                  String imagePath, InputHandler inputHandler) {

        super(x, y, width, height, imagePath);
        this.speed = speed;
        this.inputHandler = inputHandler;
        this.onGround = false;
        vx = 0;
        vy = 0;
    }

    @Override
    public void update(double dt) {
        // 1. Horizontal
        if (inputHandler.isPressed(KeyCode.LEFT)) {
            vx = -speed;
        } else if (inputHandler.isPressed(KeyCode.RIGHT)) {
            vx = speed;
        } else {
            vx = 0;
        }

        // 2. Gravité
        vy += GRAVITY * dt;

        // 3. Saut
        if (onGround && inputHandler.isPressed(KeyCode.UP)) {
            vy = -JUMP_SPEED;
            onGround = false;
        }

        // 4. Position
        x += vx * dt;
        y += vy * dt;

        // 5. Affichage
        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        System.out.println("vy=" + vy + ", y=" + y + ", onGround=" + onGround);
    }

    

    public double getSpeed() {
        return speed;
    }
}