package com.teikinui.game.model.traps.ground;

import com.teikinui.game.model.traps.Trap;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Porte qui ramène au départ du niveau. */
public final class ReturnDoor extends Trap {
    public ReturnDoor(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
    }

    /** Déplace la porte au départ, sans déplacer le joueur et une seule fois. */
    public boolean moveToStart() {
        if (state == State.DONE || (x == startX && y == startY)) return false;
        x = startX;
        y = startY;
        return true;
    }

    @Override protected boolean isTriggered() { return collidesWith(target); }
    @Override protected void activate(float dt) { }

    @Override public boolean isActive() { return false; }

    @Override public void onPlayerContact(Player player, EventBus bus) {
        if (state != State.DONE && collidesWith(player)) {
            player.teleport(startX, startY);
            state = State.DONE;
        }
    }
}
