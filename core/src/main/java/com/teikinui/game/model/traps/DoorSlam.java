package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Le bloc du puits de départ (Tunisie) : invisible tant que le joueur tombe.
 * Juste après son atterrissage en bas du puits, le bloc tombe derrière lui :
 * s'il ne part pas tout de suite, il est écrasé.
 */
public final class DoorSlam extends FallingBlock {
    private static final float FALL_SPEED = 20f;
    private static final float DELAY = 0.1f;

    private float landedFor;

    public DoorSlam(float x, float y, Player target) {
        super(x, y, target, FALL_SPEED);
    }

    private boolean playerLandedBelow() {
        boolean below = target.getY() < y;
        boolean near = target.getX() + target.getWidth() > x - GameConfig.TILE
                && target.getX() < x + width + GameConfig.TILE;
        return below && near && target.isOnGround();
    }

    @Override
    protected boolean shouldFall() {
        if (landedFor > 0f || playerLandedBelow()) {
            landedFor += 1f / 60f;
        }
        return landedFor >= DELAY;
    }
}