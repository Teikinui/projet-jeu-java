package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Faux sol (Chine) : il est dessiné exactement comme le sol, mais ce n'est pas
 * un mur : on passe à travers et on tombe dans les pics en dessous.
 */
public final class FakeSlab extends Trap {
    public FakeSlab(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        return false;
    }

    @Override
    protected void activate(float dt) {
    }

    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}