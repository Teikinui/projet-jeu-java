package com.teikinui.game.model;

/** Personnage contrôlé par le joueur, avec une gravité verticale simple. */
public class Player extends Entity {
    public static final float SPRITE_DRAW_SIZE = 64f;
    public static final float SPRITE_DRAW_Y_OFFSET = -12f;
    private static final float SPRITE_SOURCE_SIZE = 48f;
    // Union of opaque pixels across the current idle, walk and jump PNG frames.
    private static final float SPRITE_ALPHA_LEFT = 16f;
    private static final float SPRITE_ALPHA_TOP = 9f;
    private static final float SPRITE_ALPHA_RIGHT = 35f;
    private static final float SPRITE_ALPHA_BOTTOM = 39f;
    private static final float GROUND_Y = 0f;
    /** Croissant (France) : on court 60 % plus vite et on saute 15 % plus haut. */
    private static final float BOOST_SPEED = 1.6f;
    private static final float BOOST_JUMP = 1.15f;
    /** Glace (Laponie) : la vitesse ne change que petit à petit, on glisse. */
    private static final float ICE_GRIP = 3f;

    private final float moveSpeed;
    private float vx;
    private float vy;
    private boolean onGround;
    private boolean slowedNextFrame;
    private float boostTime;
    private boolean icy;

    public Player(float x, float y, float width, float height, float moveSpeed) {
        super(x, y, width, height);
        if (width <= 0f || height <= 0f) {
            throw new IllegalArgumentException("Player width and height must be positive");
        }
        this.moveSpeed = moveSpeed;
        this.onGround = y <= GROUND_Y;
        setContactBounds(spriteContactBounds(width, height));
    }

    private static ContactBounds spriteContactBounds(float width, float height) {
        float scale = SPRITE_DRAW_SIZE / SPRITE_SOURCE_SIZE;
        float drawX = (width - SPRITE_DRAW_SIZE) / 2f;
        float drawY = SPRITE_DRAW_Y_OFFSET;
        return new ContactBounds(
                (drawX + SPRITE_ALPHA_LEFT * scale) / width,
                (drawY + (SPRITE_SOURCE_SIZE - SPRITE_ALPHA_BOTTOM) * scale) / height,
                (drawX + SPRITE_ALPHA_RIGHT * scale) / width,
                (drawY + (SPRITE_SOURCE_SIZE - SPRITE_ALPHA_TOP) * scale) / height);
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
        float wanted = 0f;
        if (left != right) {
            wanted = left ? -speed : speed;
        }
        if (icy) {
            // Sur la glace, on se rapproche doucement de la vitesse voulue : on glisse.
            vx += (wanted - vx) * Math.min(1f, ICE_GRIP * deltaTime);
        } else {
            vx = wanted;
        }

        if (jump && onGround) {
            vy = isBoosted() ? GameConfig.JUMP_SPEED * BOOST_JUMP : GameConfig.JUMP_SPEED;
            onGround = false;
        }

        // La gravité négative ralentit la montée puis accélère la chute.
        vy += GameConfig.GRAVITY * deltaTime;
        boostTime = Math.max(0f, boostTime - deltaTime);
    }

    /** Active la glace : le joueur glisse (Laponie). */
    public void setIcy(boolean icy) {
        this.icy = icy;
    }

    public boolean isIcy() {
        return icy;
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