package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.model.GameConfig;

/** Le hall d'aéroport : titre, commandes, et Entrée pour partir. */
public final class MenuScreen extends BaseScreen {
    private static final float[] BACKGROUND = {0.12f, 0.16f, 0.24f};

    public MenuScreen(TripTrapGame game) {
        super(game);
        setBackground(BACKGROUND);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.startGame();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        int width = GameConfig.VIEW_WIDTH;
        batch.begin();
        game.getAssets().getTitleFont().draw(batch, "TRIP TRAP", 0, 420, width, Align.center, false);
        game.getAssets().getFont().draw(batch, "Le voyage le plus énervant du monde",
                0, 340, width, Align.center, false);
        game.getAssets().getFont().draw(batch, "Tahiti  >  Chine  >  Tunisie, en 8 minutes",
                0, 290, width, Align.center, false);
        game.getAssets().getFont().draw(batch,
                "Flèches gauche et droite : courir      flèche du haut ou barre Espace : sauter",
                0, 220, width, Align.center, false);
        game.getAssets().getFont().draw(batch, "Appuie sur Entrée pour embarquer",
                0, 130, width, Align.center, false);
        batch.end();
    }
}