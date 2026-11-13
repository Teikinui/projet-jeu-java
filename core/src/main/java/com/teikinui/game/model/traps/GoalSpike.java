package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Pic mortel de l'embuscade de Tahiti, créé seulement après le déplacement de G. */
public final class GoalSpike extends Trap {
    public GoalSpike(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, 20f, target);
        state = State.TRIGGERED;
    }

    @Override protected boolean isTriggered() { return true; }
    @Override protected void activate(float dt) { }
}
