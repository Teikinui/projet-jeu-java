package com.game;

import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Entity {

    // Position de l'entité
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // Image et affichage
    protected Image spriteSheet;
    protected ImageView sprite;

    // Constructeur
    public Entity(double x, double y, double width, double height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.spriteSheet = new Image(
            Objects.requireNonNull(
                getClass().getResourceAsStream(imagePath),
                "Image introuvable : " + imagePath
            )
        );

        this.sprite = new ImageView(spriteSheet);

        this.sprite.setTranslateX(x);
        this.sprite.setTranslateY(y);
        this.sprite.setFitWidth(width);
        this.sprite.setFitHeight(height);
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Mise à jour de l'entité
    public abstract void update(double dt);
}
