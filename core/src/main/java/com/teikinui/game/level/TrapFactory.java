package com.teikinui.game.level;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

/** Turns a letter of the level file into a trap (filled in by lot 3). */
public final class TrapFactory {
    private TrapFactory() {
    }

    public static boolean isTrap(char letter) {
        return false;
    }

    public static Trap create(char letter, float x, float y, Player target) {
        return null;
    }
}
