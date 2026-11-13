package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Mur qui fonce (Chine) : un mur de toute la hauteur part de la droite dès que le
 * joueur s'avance, traverse l'écran et s'arrête juste avant le bord gauche.
 * Seule la toute première colonne est sûre : il faut y être, sur la dalle invisible.
 * Ensuite il repart à droite et disparaît.
 */
public final class ChargingWall extends Trap {
    private static final float WAKE_DISTANCE = 22f * GameConfig.TILE;
    private static final float SPEED = 8f * GameConfig.TILE;
    private static final float BACK_SPEED = 6f * GameConfig.TILE;
    private static final float STOP_X = 2f * GameConfig.TILE + 2f;
    private static final float PAUSE = 1f;

    private final float homeX;
    private boolean goingBack;
    private float paused;

    public ChargingWall(float x, float y, Player target) {
        super(x, GameConfig.TILE, GameConfig.TILE, (GameConfig.LEVEL_ROWS - 2) * GameConfig.TILE, target);
        homeX = x;
        setVisible(false);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        return target.getX() > homeX - WAKE_DISTANCE;
    }

    @Override
    protected void activate(float dt) {
        setVisible(true);
        if (!goingBack) {
            x = Math.max(STOP_X, x - SPEED * dt);
            if (x <= STOP_X) {
                paused += dt;
                goingBack = paused >= PAUSE;
            }
            return;
        }
        x += BACK_SPEED * dt;
        if (x >= homeX) {
            state = State.DONE;
        }
    }
}