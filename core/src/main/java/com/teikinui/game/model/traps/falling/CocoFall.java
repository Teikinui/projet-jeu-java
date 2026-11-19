package com.teikinui.game.model.traps.falling;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Noix de coco tombante : reste sur l'étage qui la porte. */
public final class CocoFall extends FallingTrap {
    private final float initialY;

    public CocoFall(float x, float y, Player target) {
        super(x, y, target);
        initialY = y;
    }

    /** Premier sol sous la position initiale, sans traverser les plateformes. */
    private float floorY() {
        if (level == null) return Float.NEGATIVE_INFINITY;
        int col = (int) (x / GameConfig.TILE);
        for (int row = (int) (initialY / GameConfig.TILE) - 1; row >= 0; row--) {
            if (level.isWall(col, row)) return (row + 1) * GameConfig.TILE;
        }
        return Float.NEGATIVE_INFINITY;
    }

    @Override protected boolean isTriggered() {
        if (target.getY() < floorY() - 1f) return false;
        return super.isTriggered();
    }

    @Override protected void activate(float dt) {
        super.activate(dt);
        y = Math.max(y, floorY());
    }
}
