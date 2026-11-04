
package com.game;

import javafx.scene.input.KeyCode;

public class Player extends Entity {

    private double speed;
    private InputHandler inputHandler;

    public Player(double x, double y, double speed,
                  String imagePath, InputHandler inputHandler) {

        super(x, y, imagePath);
        this.speed = speed;
        this.inputHandler = inputHandler;
    }

    @Override
    public void update() {

        // Déplacement vers la gauche
        if (inputHandler.isPressed(KeyCode.LEFT)) {
            x -= speed;
        }

        // Déplacement vers la droite
        if (inputHandler.isPressed(KeyCode.RIGHT)) {
            x += speed;
        }

        // Déplacement vers le haut
        if (inputHandler.isPressed(KeyCode.UP)) {
            y -= speed;
        }

        // Déplacement vers le bas
        if (inputHandler.isPressed(KeyCode.DOWN)) {
            y += speed;
        }

        // On actualise la position de l'image
        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
    }

    public double getSpeed() {
        return speed;
    }
}