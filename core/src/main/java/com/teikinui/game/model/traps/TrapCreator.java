package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;

/** Knows how to build one family of traps. */
public interface TrapCreator {
    Trap create(TrapType type, float x, float y, Player target);
}
