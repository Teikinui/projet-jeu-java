package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/**
 * Dalle invisible (Chine) : c'est un vrai mur, solide dès le départ, mais on ne le
 * voit pas. Elle apparaît quand le joueur se pose dessus.
 */
public final class HiddenSlab extends Trap {
    private boolean revealed;

    public HiddenSlab(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setMarked(false);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        level.setWall(Math.round(x / GameConfig.TILE), Math.round(y / GameConfig.TILE), true);
    }

    public boolean isRevealed() {
        return revealed;
    }

    @Override
    protected boolean isTriggered() {
        boolean onTop = Math.abs(target.getY() - (y + height)) < 1f;
        boolean above = target.getX() + target.getWidth() > x && target.getX() < x + width;
        return target.isOnGround() && onTop && above;
    }

    @Override
    protected void activate(float dt) {
        revealed = true;
    }

    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}
