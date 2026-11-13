package com.teikinui.game.model.traps.trigger;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public class PlayerOnTop implements Trigger {
    @Override
    public boolean isTriggered(Trap trap, Player player) {
        return player.getX() + player.getWidth() > trap.getX()
            && player.getX() < trap.getX() + trap.getWidth()
            && player.getY() >= trap.getY() + trap.getHeight() - 5;
    }
}