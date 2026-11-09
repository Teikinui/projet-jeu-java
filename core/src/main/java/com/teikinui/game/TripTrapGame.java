package com.teikinui.game;

import com.badlogic.gdx.Game;
import com.teikinui.game.view.PlayScreen;

/** Point d'entrée du jeu : installe l'écran principal. */
public class TripTrapGame extends Game {
    @Override
    public void create() {
        setScreen(new PlayScreen());
    }
}
