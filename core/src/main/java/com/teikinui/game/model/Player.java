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

    /**
     * Calcule les vitesses selon les commandes et la gravité. La position est ensuite
     * appliquée par {@link CollisionSystem#move(Player, Level, float)}.
     */
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

        // La gravité négative ralentit la montée puis accélère la chute.
        vy += GRAVITY * deltaTime;
    }

    public float getVx() {
        return vx;
    }

    /** Annule la vitesse verticale après un contact avec le sol. */
    void land() {
        this.vy = 0f;
        this.onGround = true;
    }

    /** Annule la vitesse verticale après un contact avec un plafond. */
    void stopVerticalMovement() {
        vy = 0f;
    }

    /** Marque le joueur en l'air lorsqu'il ne touche plus de sol. */
    void leaveGround() {
        onGround = false;
    }

    /** Annule la vitesse horizontale après un contact avec un mur. */
    void stopHorizontalMovement() {
        vx = 0f;
    }

    public float getVy() {
        return vy;
    }

    public boolean isOnGround() {
        return onGround;
    }
}
