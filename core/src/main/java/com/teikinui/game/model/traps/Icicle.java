package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Stalactite de glace (Laponie) : accrochée au plafond, elle se décroche d'un coup
 * quand le joueur passe juste en dessous, tombe très vite et se brise au sol.
 */
public final class Icicle extends Trap {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 28f;
    private static final float REACH = 0.6f * GameConfig.TILE;
    private static final float FALL_SPEED = 18f * GameConfig.TILE;

    public Icicle(float x, float y, Player target) {
        super(x + (GameConfig.TILE - WIDTH) / 2f, y + GameConfig.TILE - HEIGHT, WIDTH, HEIGHT, target);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        boolean below = target.getY() < y;
        boolean near = target.getX() + target.getWidth() > x - REACH && target.getX() < x + width + REACH;
        return below && near;
    }

    @Override
    protected void activate(float dt) {
        y -= FALL_SPEED * dt;
        int col = (int) Math.floor((x + width / 2f) / GameConfig.TILE);
        int row = (int) Math.floor(y / GameConfig.TILE);
        if (y < 0f || level.isWall(col, row)) {
            // Elle se brise en touchant le sol.
            state = State.DONE;
        }
    }
}