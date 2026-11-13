package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.GameConfig;

/**
 * Le hall d'aéroport : titre, choix du pays de départ, et Entrée pour partir.
 * Flèches haut / bas : changer de pays. Le voyage continue ensuite vers les pays suivants.
 * À droite de chaque pays : sa difficulté, de 1 à 4 bombes.
 */
public final class MenuScreen extends BaseScreen {
    private static final float[] BACKGROUND = {0.12f, 0.16f, 0.24f};
    private static final float LIST_TOP = 375f;
    private static final float LINE = 27f;
    private static final float BOMBS_X = GameConfig.VIEW_WIDTH / 2f + 120f;
    private static final float BOMB_RADIUS = 6f;
    private static final float BOMB_STEP = 18f;

    private final LevelTheme[] countries = LevelTheme.values();
    private final ShapeRenderer shapes = new ShapeRenderer();
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
        game.getAssets().getTitleFont().draw(batch, "TRIP TRAP", 0, 495, width, Align.center, false);
        game.getAssets().getFont().draw(batch, "Choisis ton pays de départ :",
                0, 420, width, Align.center, false);
        for (int i = 0; i < countries.length; i++) {
            String name = countries[i].getDisplayName();
            String line = i == selected ? ">  " + name + "  <" : name;
            game.getAssets().getFont().draw(batch, line,
                    0, LIST_TOP - i * LINE, width, Align.center, false);
        }
        game.getAssets().getFont().draw(batch, "Haut / Bas : choisir le pays      Entrée : embarquer",
                0, 105, width, Align.center, false);
        game.getAssets().getFont().draw(batch,
                "En jeu : Gauche / Droite : courir, Haut ou Espace : sauter, Échap : quitter",
                0, 70, width, Align.center, false);
        batch.end();
        drawBombs();
    }

    /** Les bombes de difficulté : un rond gris foncé + une mèche orange. */
    private void drawBombs() {
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < countries.length; i++) {
            float y = LIST_TOP - i * LINE - 9f;
            for (int b = 0; b < countries[i].getDifficulty(); b++) {
                float x = BOMBS_X + b * BOMB_STEP;
                shapes.setColor(0.80f, 0.82f, 0.86f, 1f);
                shapes.circle(x, y, BOMB_RADIUS + 1f);
                shapes.setColor(0.10f, 0.10f, 0.12f, 1f);
                shapes.circle(x, y, BOMB_RADIUS);
                shapes.setColor(1f, 0.55f, 0.10f, 1f);
                shapes.rect(x + 3f, y + 4f, 3f, 5f);
            }
        }
        shapes.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
        super.dispose();
    }
}