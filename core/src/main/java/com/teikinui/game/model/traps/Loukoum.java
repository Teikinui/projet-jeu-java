package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Loukoum collant (Turquie) : une couche rose posée sur le sol.
 * Tant que le joueur marche dessus, il avance deux fois moins vite.
 * Il ne tue pas, mais il rend les autres pièges (derviche, simits) bien plus durs.
 */
public final class Loukoum extends Trap {
    private static final float THICKNESS = 8f;

    public Loukoum(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, THICKNESS, target);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        return true;
    }

    @Override
    protected void activate(float dt) {
        if (collidesWith(target)) {
            target.slowNextFrame();
        }
    }

    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}