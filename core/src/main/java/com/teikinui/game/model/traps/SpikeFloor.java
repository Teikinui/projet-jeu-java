package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Sol de pics (Vietnam) : toujours visible, toujours mortel. On ne doit jamais tomber dessus. */
public final class SpikeFloor extends Trap {
    private static final float HEIGHT = 20f;

    public SpikeFloor(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, HEIGHT, target);
    }

    @Override
    protected boolean isTriggered() {
        return true;
    }

    @Override
    protected void activate(float dt) {
        // Rien à faire : les pics sont déjà sortis.
    }
}