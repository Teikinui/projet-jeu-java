package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Faux objectif (Sri Lanka) : il ressemble exactement au bâton de cannelle,
 * mais le toucher tue. Le vrai bâton de cannelle est ailleurs.
 */

public final class FakeChili extends Trap {
    public FakeChili(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        return true;
    }

    @Override
    protected void activate(float dt) {
        // Il ne bouge pas : il attend qu'on le prenne pour le vrai.
    }
}