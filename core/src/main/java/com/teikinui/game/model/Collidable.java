package com.teikinui.game.model;

/** Something that can touch other things. */
public interface Collidable {
    Hitbox getHitbox();

    default boolean collidesWith(Collidable other) {
        return getHitbox().overlaps(other.getHitbox());
    }
}
