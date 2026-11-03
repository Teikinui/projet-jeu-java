package com.example.javafxdemo;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Entity {
    private Point2D position = Point2D.ZERO;
    protected final ImageView sprite;
    protected final Image spriteSheet;

    protected Entity(String spritePath, Point2D position) {
        var imageResource = Entity.class.getResource(spritePath);
        if (imageResource == null) {
            throw new IllegalArgumentException("Image introuvable : " + spritePath);
        }

        this.spriteSheet = new Image(imageResource.toExternalForm());
        this.sprite = new ImageView(spriteSheet);
        setPosition(position);
    }

    public Point2D getPosition() {
        return position;
    }

    protected final void setPosition(Point2D position) {
        this.position = position;
        sprite.setTranslateX(position.getX());
        sprite.setTranslateY(position.getY());
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public abstract void update();


}
