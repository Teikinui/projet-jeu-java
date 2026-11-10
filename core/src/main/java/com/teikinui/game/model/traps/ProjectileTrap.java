package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Projectile horizontal lancé vers le joueur proche. */
public class ProjectileTrap extends Trap {
    private float direction;

    public ProjectileTrap(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setTrigger((trap, player) -> Math.abs(player.getX() - trap.getX()) <= GameConfig.TILE * 4
                && Math.abs(player.getY() - trap.getY()) < GameConfig.TILE);
        setVisible(false);
    }

    @Override protected boolean isTriggered() {
        if (!triggeredByPlayer()) return false;
        direction = target.getX() >= x ? 1f : -1f;
        setVisible(true);
        return true;
    }

    @Override protected void activate(float dt) {
        x += direction * GameConfig.TILE * 4f * dt;
    }
}
