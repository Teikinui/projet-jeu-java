package com.teikinui.game.model.traps.ground;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public class HiddenDalle extends Trap {
    public HiddenDalle(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
    }

    // Elle n'est jamais "déclenchée" par un trigger, elle est toujours là (solide)
    @Override protected boolean isTriggered() { return false; }
    @Override protected void activate(float dt) { }
    @Override public boolean isActive() { return false; } // Ne tue pas

    // On triche : on dit qu'elle est un mur pour que la collision la bloque
    // (Il faut que ton CollisionSystem vérifie ça, sinon ça ne bloquera pas)
}