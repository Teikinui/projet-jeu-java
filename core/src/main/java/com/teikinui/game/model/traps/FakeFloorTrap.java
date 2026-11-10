package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Leur-re au sol qui disparaît au contact sans blesser. */
public final class FakeFloorTrap extends Trap {
    public FakeFloorTrap(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
    }

    @Override protected boolean isTriggered() { return collidesWith(target); }
    @Override protected void activate(float dt) { }

    @Override public boolean isActive() { return false; }

    @Override public void onPlayerContact(Player player, EventBus bus) {
        if (state != State.DONE && collidesWith(player)) state = State.DONE;
    }
}
