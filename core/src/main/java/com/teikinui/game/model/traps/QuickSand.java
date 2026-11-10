package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Sable mouvant : ralentit pendant le contact, sans tuer. */
public final class QuickSand extends Trap {
    public QuickSand(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
    }

    @Override protected boolean isTriggered() { return collidesWith(target); }
    @Override protected void activate(float dt) { }

    @Override public void onPlayerContact(Player player, EventBus bus) {
        if (collidesWith(player)) player.slowNextFrame();
        else state = State.WAITING;
    }

    @Override public boolean isActive() { return false; }

    @Override public boolean isMarked() { return true; }
}
