package com.teikinui.game.model.traps.ground;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

/** Dalle qui fait tomber (disparaît au contact). */
public final class TrapDalle extends Trap {
    public TrapDalle(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setTrigger((trap, player) -> collidesWith(player));
    }

    @Override
    protected boolean isTriggered() {
        return triggeredByPlayer();
    }

    @Override
    protected void activate(float dt) {
        state = State.DONE; // la dalle disparaît
    }

    @Override
    public boolean isActive() {
        return false; // ne tue pas au contact
    }
}