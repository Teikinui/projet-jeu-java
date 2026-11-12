package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Pic caché sur un bloc (Vietnam) : invisible, il surgit quand le joueur
 * arrive à côté. Il faut alors sauter par-dessus au lieu d'atterrir dessus.
 */
public final class PopSpike extends Trap {
    private static final float SIZE = 24f;
    private static final float REACH = 1.5f * GameConfig.TILE;

    public PopSpike(float x, float y, Player target) {
        super(x + (GameConfig.TILE - SIZE) / 2f, y, SIZE, SIZE, target);
        setVisible(false);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        boolean near = target.getX() + target.getWidth() > x - REACH
                && target.getX() < x + width + REACH;
        boolean sameFloor = target.getY() > y - GameConfig.TILE
                && target.getY() < y + 4 * GameConfig.TILE;
        return near && sameFloor;
    }

    @Override
    protected void activate(float dt) {
        setVisible(true);
    }
}