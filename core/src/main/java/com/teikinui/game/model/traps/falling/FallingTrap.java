package com.teikinui.game.model.traps.falling;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

/** Objet suspendu qui tombe lorsque le joueur est proche sous lui. */
public class FallingTrap extends Trap {
    public FallingTrap(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setTrigger((trap, player) -> Math.abs(player.getX() - trap.getX()) < GameConfig.TILE * 2
                && player.getY() < trap.getY());
        // setVisible(false); ← SUPPRIMÉ pour que le piège soit visible
    }

    @Override
    protected boolean isTriggered() {
        if (!triggeredByPlayer()) return false;
        setVisible(true);
        return true;
    }

    @Override
    protected void activate(float dt) {
        y -= GameConfig.TILE * 4f * dt;
    }
}