package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Dune (Sri Lanka) : du sable qui a l'air d'un sol normal. Dès qu'on marche
 * dessus, il s'effondre, et toute la dune s'écroule de proche en proche.
 */
public final class Dune extends FallingBlock {
    private static final char LETTER = 'w';
    private static final float FALL_SPEED = 6f;
    private static final float DELAY = 0.12f;

    private float collapsingFor;

    public Dune(float x, float y, Player target) {
        super(x, y, target, FALL_SPEED);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        level.setWall(col, row, true);
    }

    private boolean playerStandsOnIt() {
        boolean onTop = Math.abs(target.getY() - (y + height)) < 1f;
        boolean above = target.getX() + target.getWidth() > x && target.getX() < x + width;
        return target.isOnGround() && onTop && above;
    }

    /** Une dune voisine a déjà commencé à tomber : son mur a disparu. */
    private boolean neighbourCollapsed(int side) {
        return level.getTrapLetter(col + side, row) == LETTER && !level.isWall(col + side, row);
    }

    @Override
    protected boolean shouldFall() {
        if (collapsingFor > 0f || playerStandsOnIt() || neighbourCollapsed(-1) || neighbourCollapsed(1)) {
            collapsingFor += 1f / 60f;
        }
        return collapsingFor >= DELAY;
    }

    @Override
    protected boolean staysAsWall() {
        return false;
    }

    /** Le sable ne tue pas : ce sont les pics en dessous qui tuent. */
    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}