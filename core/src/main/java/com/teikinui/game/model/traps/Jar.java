package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Jarre accrochée au plafond (Tunisie). Elle tombe quand le joueur approche,
 * puis devient une marche. Après une mort, elle tombe une case plus tôt.
 */
public final class Jar extends Trap {
    private static final float FALL_SPEED = GameConfig.TILE * 6f;
    private static final int TILES_BEFORE = 2;

    private final int col;
    private final int row;
    private boolean detached;

    public Jar(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        col = Math.round(x / GameConfig.TILE);
        row = Math.round(y / GameConfig.TILE);
        setVisible(false);
    }

    /** Au chargement, la jarre bouche sa case du plafond. */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        level.setWall(col, row, true);
    }

    /** Distance de déclenchement : 2 cases, ou 3 si le joueur est déjà mort ici. */
    float triggerDistance() {
        int tiles = TILES_BEFORE;
        if (level != null && level.getAttempt() > 0) {
            tiles++;
        }
        return tiles * GameConfig.TILE;
    }

    @Override
    protected boolean isTriggered() {
        boolean below = target.getY() < y;
        boolean close = target.getX() + target.getWidth() >= x - triggerDistance()
                && target.getX() <= x + width;
        return below && close;
    }

    @Override
    protected void activate(float dt) {
        if (!detached) {
            detached = true;
            level.setWall(col, row, false);
            setVisible(true);
        }
        y -= FALL_SPEED * dt;
        int belowRow = (int) Math.floor((y - 1f) / GameConfig.TILE);
        float floorTop = (belowRow + 1) * GameConfig.TILE;
        if (level.isWall(col, belowRow) && y <= floorTop) {
            y = floorTop;
            if (collidesWith(target)) {
                return;
            }
            level.setWall(col, belowRow + 1, true);
            state = State.DONE;
        }
    }
}