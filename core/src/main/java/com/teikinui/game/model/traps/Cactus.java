package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Cactus caché dans le sable (Tunisie) : invisible, il jaillit quand le joueur
 * arrive à côté de lui au niveau du sol. On l'évite en sautant par-dessus.
 */
public final class Cactus extends Trap {
    private static final float REACH = GameConfig.TILE;

    public Cactus(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setVisible(false);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        boolean low = target.getY() < y + height;
        boolean near = target.getX() + target.getWidth() > x - REACH
                && target.getX() < x + width + REACH;
        return low && near;
    }

    @Override
    protected void activate(float dt) {
        setVisible(true);
    }
}