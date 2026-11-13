package com.teikinui.game.model.traps.trigger;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public interface Trigger {
    boolean isTriggered(Trap trap, Player player);  // ← Trap d'abord, Player ensuite
}