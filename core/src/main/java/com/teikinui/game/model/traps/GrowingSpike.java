package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Pilier de pics (Vietnam) : caché, il jaillit quand le joueur passe à côté.
 * Du sol : il monte de 2 cases et reste sorti (il faut sauter par-dessus).
 * Du plafond : c'est un pilon, il descend jusqu'au sol très vite, remonte,
 * attend un peu puis recommence, sans fin. Il faut passer quand il est en haut.
 */
public final class GrowingSpike extends Trap {
    private static final float THICKNESS = 16f;
    private static final float FLOOR_LENGTH = 2f * GameConfig.TILE;
    private static final float MAX_CEILING_LENGTH = 5f * GameConfig.TILE;
    private static final float GROW_SPEED = 12f * GameConfig.TILE;
    private static final float DROP_SPEED = 24f * GameConfig.TILE;
    private static final float RISE_SPEED = 8f * GameConfig.TILE;
    private static final float HOLD_DOWN = 0.25f;
    private static final float PAUSE_UP = 0.9f;
    private static final float REACH = 1.5f * GameConfig.TILE;
    private static final float SIGHT = 5f * GameConfig.TILE;

    private enum Phase { DOWN, HOLD, UP, PAUSE }

    private final float tileX;
    private final float tileY;
    private boolean fromCeiling;
    private float maxLength = FLOOR_LENGTH;
    private float length;
    private Phase phase = Phase.DOWN;
    private float phaseTime;

    public GrowingSpike(float x, float y, Player target) {
        super(x + (GameConfig.TILE - THICKNESS) / 2f, y, THICKNESS, 0f, target);
        tileX = x;
        tileY = y;
        setVisible(false);
        setMarked(false);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        int col = Math.round(tileX / GameConfig.TILE);
        int row = Math.round(tileY / GameConfig.TILE);
        fromCeiling = level.isWall(col, row + 1);
        if (fromCeiling) {
            y = tileY + GameConfig.TILE;
            int free = 0;
            while (row - free >= 0 && !level.isWall(col, row - free)) {
                free++;
            }
            maxLength = Math.min(MAX_CEILING_LENGTH, free * GameConfig.TILE);
        }
    }

    public boolean isFromCeiling() {
        return fromCeiling;
    }

    /** Le joueur est juste à côté ET dans le même couloir (pas à l'étage du dessous). */
    @Override
    protected boolean isTriggered() {
        boolean near = target.getX() + target.getWidth() > tileX - REACH
                && target.getX() < tileX + GameConfig.TILE + REACH;
        float bottom = target.getY();
        float top = bottom + target.getHeight();
        boolean sameRoom = fromCeiling
                ? top > tileY + GameConfig.TILE - SIGHT && bottom < tileY + GameConfig.TILE
                : bottom < tileY + SIGHT && top > tileY;
        return near && sameRoom;
    }

    @Override
    protected void activate(float dt) {
        setVisible(true);
        if (fromCeiling) {
            pound(dt);
        } else {
            length = Math.min(maxLength, length + GROW_SPEED * dt);
        }
        height = length;
        y = fromCeiling ? tileY + GameConfig.TILE - length : tileY;
    }

    /** Le cycle du pilon : descend, reste en bas, remonte, attend en haut, recommence. */
    private void pound(float dt) {
        phaseTime += dt;
        switch (phase) {
            case DOWN -> {
                length = Math.min(maxLength, length + DROP_SPEED * dt);
                if (length >= maxLength) {
                    next(Phase.HOLD);
                }
            }
            case HOLD -> {
                if (phaseTime >= HOLD_DOWN) {
                    next(Phase.UP);
                }
            }
            case UP -> {
                length = Math.max(0f, length - RISE_SPEED * dt);
                if (length <= 0f) {
                    next(Phase.PAUSE);
                }
            }
            case PAUSE -> {
                if (phaseTime >= PAUSE_UP) {
                    next(Phase.DOWN);
                }
            }
        }
    }

    private void next(Phase newPhase) {
        phase = newPhase;
        phaseTime = 0f;
    }
}