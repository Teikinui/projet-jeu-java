package com.teikinui.game.model;

/** Base commune des objets du monde, indépendante de libGDX et de JavaFX. */
public abstract class Entity implements Updatable, Collidable {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    private ContactBounds contactBounds = ContactBounds.FULL;

    protected Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Hitbox calculée à partir de la position actuelle, y compris après les collisions. */
    @Override
    public Hitbox getHitbox() {
        return new Hitbox(
                x + contactBounds.left() * width,
                y + contactBounds.bottom() * height,
                (contactBounds.right() - contactBounds.left()) * width,
                (contactBounds.top() - contactBounds.bottom()) * height);
    }

    /** Sets the visible/contact rectangle as fractions of the entity's movement rectangle. */
    public void setContactBounds(ContactBounds contactBounds) {
        this.contactBounds = java.util.Objects.requireNonNull(contactBounds);
    }

    /** Mise à jour de l'entité pour le temps écoulé depuis la frame précédente. */
    public abstract void update(float deltaTime);

    /** Retourne true lorsque les rectangles des deux entités se chevauchent. */
    public boolean intersects(Entity other) {
        return getHitbox().overlaps(other.getHitbox());
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
