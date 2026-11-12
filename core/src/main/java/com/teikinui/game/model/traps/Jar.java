package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Jarre du plafond (Tunisie). Elle tombe d'un coup quand le joueur arrive juste
 * à côté, puis devient une marche. Deux jarres côte à côte tombent ensemble.
 * Après une mort, elle attend que le joueur soit encore plus près : le piège apprend.
 */
public final class Jar extends FallingBlock {
    private static final float FALL_SPEED = 20f;
    private static final float REACH = 0.5f;
    private static final float REACH_AFTER_DEATH = 0.25f;

    public Jar(float x, float y, Player target) {
        super(x, y, target, FALL_SPEED);
    }

    /** Au chargement, la jarre bouche sa case du plafond. */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        level.setWall(col, row, true);
    }

    /** Distance de déclenchement : ½ case, ou ¼ de case si le joueur est déjà mort ici. */
    float triggerDistance() {
        boolean learned = level != null && level.getAttempt() > 0;
        return (learned ? REACH_AFTER_DEATH : REACH) * GameConfig.TILE;
    }

    /** La jarre de gauche est tombée : on tombe avec elle. */
    private boolean leftJarFell() {
        return level.getTrapLetter(col - 1, row) == TrapType.JAR.getLetter()
                && !level.isWall(col - 1, row);
    }

    @Override
    protected boolean shouldFall() {
        boolean below = target.getY() < y;
        boolean close = target.getX() + target.getWidth() >= x - triggerDistance()
                && target.getX() <= x + width;
        return below && close || leftJarFell();
    }
}