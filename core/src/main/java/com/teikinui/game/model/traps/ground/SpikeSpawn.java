package com.teikinui.game.model.traps.ground;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public final class SpikeSpawn extends Trap {
    public SpikeSpawn(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setTrigger((trap, player) -> collidesWith(player)); // S'active au contact
    }
    @Override protected boolean isTriggered() { return triggeredByPlayer(); }
    @Override protected void activate(float dt) { }
    @Override public boolean isActive() { return true; } // Tue au contact
}