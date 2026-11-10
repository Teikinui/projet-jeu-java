package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;

/** The trap families (one line per family, added by lots 2 and 3). */
public enum TrapFamily implements TrapCreator {
    ;

    private final TrapCreator creator;

    TrapFamily(TrapCreator creator) {
        this.creator = creator;
    }

    @Override
    public Trap create(TrapType type, float x, float y, Player target) {
        return creator.create(type, x, y, target);
    }
}
