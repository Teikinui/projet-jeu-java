package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Dalle qui bascule (Chine) : une dalle normale. Peu après que le joueur s'y pose,
 * elle se penche (toutes ses cases ensemble) et le joueur glisse dans le trou dessous.
 */
public final class TiltSlab extends Trap {
    private static final char LETTER = 'b';
    private static final float DELAY = 0.3f;
    private static final float TILT_SPEED = 4f;

    private final int col;
    private final int row;
    private int index;
    private float standingFor;
    private float tilt;

    public TiltSlab(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        col = Math.round(x / GameConfig.TILE);
        row = Math.round(y / GameConfig.TILE);
        setMarked(false);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        level.setWall(col, row, true);
        while (level.getTrapLetter(col - index - 1, row) == LETTER) {
            index++;
        }
    }

    /** Place de la case dans la dalle (0 = la plus à gauche), pour dessiner la pente. */
    public int getIndex() {
        return index;
    }

    /** 0 = à plat, 1 = complètement penchée. */
    public float getTilt() {
        return tilt;
    }

    private boolean playerStandsOnIt() {
        boolean onTop = Math.abs(target.getY() - (y + height)) < 1f;
        boolean above = target.getX() + target.getWidth() > x && target.getX() < x + width;
        return target.isOnGround() && onTop && above;
    }

    private boolean neighbourTilted(int side) {
        return level.getTrapLetter(col + side, row) == LETTER && !level.isWall(col + side, row);
    }

    @Override
    protected boolean isTriggered() {
        if (standingFor > 0f || playerStandsOnIt()) {
            standingFor += 1f / 60f;
        }
        return standingFor >= DELAY || neighbourTilted(-1) || neighbourTilted(1);
    }

    @Override
    protected void activate(float dt) {
        level.setWall(col, row, false);
        tilt = Math.min(1f, tilt + TILT_SPEED * dt);
    }

    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}