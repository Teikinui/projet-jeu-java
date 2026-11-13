package com.teikinui.game.model.traps.ground;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public final class FakeDalle extends Trap {
    public FakeDalle(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
    }

    @Override protected boolean isTriggered() { return true; } // Toujours "active"
    @Override protected void activate(float dt) { }
    @Override public boolean isActive() { return false; } // Ne tue pas au contact
    // Elle est juste traversable. La chute mortelle fera le reste.
}