package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Derviche tourneur (Turquie) : il tourne sur lui-même et fait des allers-retours
 * sur le sol (3 cases de chaque côté). Le toucher tue : il faut le sauter au bon moment.
 */
public final class Dervish extends Trap {
    private static final float WIDTH = 24f;
    private static final float HEIGHT = 30f;
    private static final float SPEED = 2.5f * GameConfig.TILE;
    private static final float RANGE = 3f * GameConfig.TILE;

    private final float leftLimit;
    private final float rightLimit;
    private float direction = 1f;
    private float spin;

    public Dervish(float x, float y, Player target) {
        super(x + (GameConfig.TILE - WIDTH) / 2f, y, WIDTH, HEIGHT, target);
        leftLimit = this.x - RANGE;
        rightLimit = this.x + RANGE;
        setMarked(false);
    }

    /** Angle de rotation, seulement pour le dessin. */
    public float getSpin() {
        return spin;
    }

    @Override
    protected boolean isTriggered() {
        return true;
    }

    @Override
    protected void activate(float dt) {
        spin += 720f * dt;
        x += direction * SPEED * dt;
        if (x <= leftLimit) {
            x = leftLimit;
            direction = 1f;
        } else if (x >= rightLimit) {
            x = rightLimit;
            direction = -1f;
        } else if (wallAhead()) {
            direction = -direction;
        }
    }

    /** Un mur juste devant ses pieds : il fait demi-tour. */
    private boolean wallAhead() {
        float front = direction > 0 ? x + width : x;
        int col = (int) Math.floor(front / GameConfig.TILE);
        int row = (int) Math.floor((y + 1f) / GameConfig.TILE);
        return level.isWall(col, row);
    }
}