package com.teikinui.game.model;

/** Base commune des objets du monde, indépendante de libGDX et de JavaFX. */
public abstract class Entity implements Updatable, Collidable {
    protected float x;
    protected float y;
    protected float width;
    protected float height;

    protected Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Hitbox calculée à partir de la position actuelle, y compris après les collisions. */
    @Override
    public Hitbox getHitbox() {
        return new Hitbox(x, y, width, height);
    }

    /** Mise à jour de l'entité pour le temps écoulé depuis la frame précédente. */
    public abstract void update(float deltaTime);

    /** Retourne true lorsque les rectangles des deux entités se chevauchent. */
    public boolean intersects(Entity other) {
        return x < other.x + other.width
            && x + width > other.x
            && y < other.y + other.height
            && y + height > other.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
