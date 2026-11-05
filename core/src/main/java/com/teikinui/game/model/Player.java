package com.teikinui.game.model;

/** Personnage contrôlé par le joueur, avec une gravité verticale simple. */
public class Player extends Entity {
    private static final float GRAVITY = -980f;
    private static final float JUMP_SPEED = 600f;
    private static final float GROUND_Y = 0f;

    private final float moveSpeed;
    private float vx;
    private float vy;
    private boolean onGround;

    public Player(float x, float y, float width, float height, float moveSpeed) {
        super(x, y, width, height);
        this.moveSpeed = moveSpeed;
        this.onGround = y <= GROUND_Y;
    }

    /** Update générique de Entity : applique la gravité sans entrée clavier. */
    @Override
    public void update(float deltaTime) {
        update(deltaTime, false, false, false);
    }

    /** Met à jour le déplacement horizontal, le saut et la gravité. */
    public void update(float deltaTime, boolean left, boolean right, boolean jump) {
        if (left == right) {
            vx = 0f;
        } else {
            vx = left ? -moveSpeed : moveSpeed;
        }

        if (jump && onGround) {
            vy = JUMP_SPEED;
            onGround = false;
        }

        vy += GRAVITY * deltaTime;
        x += vx * deltaTime;
        y += vy * deltaTime;

        // Sol temporaire à Y=0 en attendant le système de collisions du niveau.
        if (y <= GROUND_Y) {
            y = GROUND_Y;
            vy = 0f;
            onGround = true;
        }
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
