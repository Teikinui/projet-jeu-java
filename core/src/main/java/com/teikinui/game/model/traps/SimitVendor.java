package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Vendeur de simits (Turquie) : il lance ses simits (pains ronds au sésame)
 * au ras du sol, droit vers le joueur, toutes les 1,6 s. Il faut sauter par-dessus.
 * Le piège (sa hitbox) est le simit : c'est lui qui tue.
 */
public final class SimitVendor extends Trap {
    private static final float SIMIT_WIDTH = 16f;
    private static final float SIMIT_HEIGHT = 10f;
    private static final float SPEED = 6f * GameConfig.TILE;
    private static final float RANGE = 14f * GameConfig.TILE;
    private static final float COOLDOWN = 1.6f;

    private final float homeX;
    private final float homeY;
    private float vx;
    private float cooldown = 0.5f;
    private boolean flying;

    public SimitVendor(float x, float y, Player target) {
        super(x + 8f, y + 6f, SIMIT_WIDTH, SIMIT_HEIGHT, target);
        homeX = x;
        homeY = y;
        setMarked(false);
    }

    public float getHomeX() {
        return homeX;
    }

    public float getHomeY() {
        return homeY;
    }

    /** Le joueur est dans le même couloir, pas trop loin. */
    private boolean playerInLane() {
        return Math.abs(target.getY() - homeY) < 2f * GameConfig.TILE
                && Math.abs(target.getX() - homeX) < RANGE;
    }

    @Override
    protected boolean isTriggered() {
        return playerInLane();
    }

    @Override
    protected void activate(float dt) {
        if (flying) {
            x += vx * dt;
            if (hitsWall()) {
                backToTray();
            }
            return;
        }
        cooldown -= dt;
        if (cooldown <= 0f && playerInLane()) {
            vx = target.getX() < homeX ? -SPEED : SPEED;
            flying = true;
            cooldown = COOLDOWN;
        }
    }

    private boolean hitsWall() {
        int col = (int) Math.floor((x + SIMIT_WIDTH / 2f) / GameConfig.TILE);
        int row = (int) Math.floor((y + SIMIT_HEIGHT / 2f) / GameConfig.TILE);
        return level.isWall(col, row);
    }

    private void backToTray() {
        flying = false;
        x = homeX + 8f;
    }
}