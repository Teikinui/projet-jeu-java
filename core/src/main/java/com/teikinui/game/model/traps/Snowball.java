package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Boule de neige (Laponie) : posée au sol, elle se met à rouler vers le joueur
 * dès qu'il entre dans le même couloir (moins de 12 cases), et se brise contre un mur.
 * La toucher tue : il faut la sauter.
 */
public final class Snowball extends Trap {
    private static final float SIZE = 26f;
    private static final float RANGE = 12f * GameConfig.TILE;
    private static final float SPEED = 5f * GameConfig.TILE;

    private float direction;
    private float spin;

    public Snowball(float x, float y, Player target) {
        super(x + (GameConfig.TILE - SIZE) / 2f, y, SIZE, SIZE, target);
        setMarked(false);
    }

    /** Angle de rotation, seulement pour le dessin. */
    public float getSpin() {
        return spin;
    }

    @Override
    protected boolean isTriggered() {
        boolean sameFloor = Math.abs(target.getY() - y) < 1.5f * GameConfig.TILE;
        return sameFloor && Math.abs(target.getX() - x) < RANGE;
    }

    @Override
    protected void activate(float dt) {
        if (direction == 0f) {
            direction = target.getX() < x ? -1f : 1f;
        }
        x += direction * SPEED * dt;
        spin += direction * -540f * dt;
        float front = direction > 0 ? x + width : x;
        int col = (int) Math.floor(front / GameConfig.TILE);
        int row = (int) Math.floor((y + 2f) / GameConfig.TILE);
        if (level.isWall(col, row)) {
            state = State.DONE;
        }
    }
}