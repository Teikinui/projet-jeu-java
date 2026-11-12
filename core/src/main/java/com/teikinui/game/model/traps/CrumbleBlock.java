package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Bloc piégé (Vietnam) : il ressemble à un mur normal, mais peu après que le
 * joueur a posé les pieds dessus, il tombe dans les pics. Il faut repartir vite !
 */
public final class CrumbleBlock extends FallingBlock {
    private static final float FALL_SPEED = 10f;
    private static final float DELAY = 0.35f;

    private float steppedFor;

    public CrumbleBlock(float x, float y, Player target) {
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

    @Override
    protected boolean shouldFall() {
        if (steppedFor > 0f || playerStandsOnIt()) {
            steppedFor += 1f / 60f;
        }
        return steppedFor >= DELAY;
    }

    /** Il disparaît dans les pics au lieu de devenir une marche. */
    @Override
    protected boolean staysAsWall() {
        return false;
    }

    /** Ce bloc ne tue pas lui-même : ce sont les pics en dessous qui tuent. */
    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}