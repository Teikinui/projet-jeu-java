package com.teikinui.game.model.traps.triggers;

import com.teikinui.game.model.traps.Trap;

import com.teikinui.game.model.Player;

/** Decides when a trap goes off. */
public interface Trigger {
    boolean isTriggered(Trap trap, Player player);
}
