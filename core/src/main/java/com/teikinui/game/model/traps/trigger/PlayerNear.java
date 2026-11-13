package com.teikinui.game.model.traps.trigger;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public class PlayerNear implements Trigger {
    private float distance;

    public PlayerNear(float distance) {
        this.distance = distance;
    }

    @Override
    public boolean isTriggered(Trap trap, Player player) {
        return Math.abs(player.getX() - trap.getX()) < distance;
    }
}