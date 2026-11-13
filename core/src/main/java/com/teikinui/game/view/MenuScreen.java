package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.GameConfig;

/**
 * Le hall d'aéroport : titre, choix du pays de départ, et Entrée pour partir.
 * Flèches haut / bas : changer de pays. Le voyage continue ensuite vers les pays suivants.
 */
public final class MenuScreen extends BaseScreen {
    private static final float[] BACKGROUND = {0.12f, 0.16f, 0.24f};
    private static final float LIST_TOP = 365f;
    private static final float LINE = 28f;

    private final LevelTheme[] countries = LevelTheme.values();
    private int selected;

    public MenuScreen(TripTrapGame game) {
        super(game);
        setBackground(BACKGROUND);
    }

    /** Le pays actuellement choisi (utile pour les tests et l'affichage). */
    public LevelTheme getSelected() {
        return countries[selected];
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selected = (selected + 1) % countries.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selected = (selected - 1 + countries.length) % countries.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.startGame(getSelected());
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        int width = GameConfig.VIEW_WIDTH;
        batch.begin();
        game.getAssets().getTitleFont().draw(batch, "TRIP TRAP", 0, 490, width, Align.center, false);
        game.getAssets().getFont().draw(batch, "Choisis ton pays de départ :",
                0, 410, width, Align.center, false);
        for (int i = 0; i < countries.length; i++) {
            String name = countries[i].getDisplayName();
            String line = i == selected ? ">  " + name + "  <" : name;
            game.getAssets().getFont().draw(batch, line,
                    0, LIST_TOP - i * LINE, width, Align.center, false);
        }
        game.getAssets().getFont().draw(batch, "Haut / Bas : choisir le pays      Entrée : embarquer",
                0, 105, width, Align.center, false);
        game.getAssets().getFont().draw(batch,
                "En jeu : Gauche / Droite : courir, flèche du haut ou barre Espace : sauter",
                0, 70, width, Align.center, false);
        batch.end();
    }
}