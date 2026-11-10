package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;

/** Étoile de mer lancée comme un shuriken. */
public final class Starfish extends ProjectileTrap {
    public Starfish(float x, float y, Player target) {
        super(x, y, target);
    }
}
