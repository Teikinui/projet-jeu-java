package com.teikinui.game.model;

/** Personnage contrôlé par le joueur, avec une gravité verticale simple. */
public class Player extends Entity {
    private static final float GROUND_Y = 0f;
    /** Croissant (France) : on court 60 % plus vite et on saute 15 % plus haut. */
    private static final float BOOST_SPEED = 1.6f;
    private static final float BOOST_JUMP = 1.15f;

    private final float moveSpeed;
    private float vx;
    private float vy;
    private boolean onGround;
    private boolean slowedNextFrame;
    private float boostTime;

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

    /** Commandes injectables, sans dépendance au clavier libGDX. */
    public void update(float deltaTime, PlayerInput input) {
        update(deltaTime, input.isLeft(), input.isRight(), input.isJump());
    }

    /**
     * Calcule les vitesses selon les commandes et la gravité. La position est ensuite
     * appliquée par {@link CollisionSystem#move(Player, Level, float)}.
     */
    public void update(float deltaTime, boolean left, boolean right, boolean jump) {
        float speed = isBoosted() ? moveSpeed * BOOST_SPEED : moveSpeed;
        if (slowedNextFrame) {
            speed *= 0.5f;
        }
        slowedNextFrame = false;
        if (left == right) {
            vx = 0f;
        } else {
            vx = left ? -speed : speed;
        }

        if (jump && onGround) {
            vy = isBoosted() ? GameConfig.JUMP_SPEED * BOOST_JUMP : GameConfig.JUMP_SPEED;
            onGround = false;
        }

        // La gravité négative ralentit la montée puis accélère la chute.
        vy += GameConfig.GRAVITY * deltaTime;
        boostTime = Math.max(0f, boostTime - deltaTime);
    }

    /** Donne un boost (plus rapide, saute plus haut) pendant quelques secondes. */
    public void boost(float seconds) {
        boostTime = seconds;
    }

    public boolean isBoosted() {
        return boostTime > 0f;
    }

    /** Ralentissement temporaire provoqué par le sable mouvant. */
    public void slowNextFrame() {
        slowedNextFrame = true;
    }

    /** Replace le joueur et remet à zéro sa vitesse (porte de retour). */
    public void teleport(float x, float y) {
        this.x = x;
        this.y = y;
        vx = 0f;
        vy = 0f;
        onGround = false;
        slowedNextFrame = false;
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