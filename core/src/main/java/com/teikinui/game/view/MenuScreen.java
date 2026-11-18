package com.teikinui.game.view;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.GameConfig;

/** Hall d'aéroport : choix du pays, tableau des départs et commande d'embarquement. */
public final class MenuScreen extends BaseScreen {
    private static final String GATES = "ABCDEFGH";
    private static final float TABLE_X = 424f;
    private static final float TABLE_WIDTH = 500f;
    private static final float BOMB_X = 792f;
    private static final float BOMB_STEP = 18f;
    private static final float BOMB_SIZE = 12f;
    static final float MENU_BUTTON_TOP_FROM_TOP = 268f;
    static final float TAGLINE_BLOCK_HEIGHT = 46f;
    static final float TAGLINE_BUTTON_GAP = 8f;

    private final LevelTheme[] countries = LevelTheme.values();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private int selected;

    public MenuScreen(TripTrapGame game) {
        super(game);
    }

    /** Le pays actuellement choisi (utile pour les tests et l'affichage). */
    public LevelTheme getSelected() {
        return countries[selected];
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selected = (selected + 1) % countries.length;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selected = (selected - 1 + countries.length) % countries.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.startGame(getSelected());
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        Assets assets = game.getAssets();
        batch.begin();
        drawBackground(batch, assets);
        drawHeading(batch, assets);
        drawDepartureBoard(batch, assets);
        drawCredits(batch, assets);
        batch.end();
        drawFallbackBombs(assets);
    }

    private void drawBackground(SpriteBatch batch, Assets assets) {
        if (assets.hasTexture("menu_bg")) {
            batch.draw(assets.getTexture("menu_bg"), 0f, 0f,
                    GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);
            return;
        }

        assets.fill(batch, Palette.HALL, 0f, 0f, GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);
        assets.fill(batch, Palette.BOARD_ROW, TABLE_X, top(462f), TABLE_WIDTH, 180f);
        assets.fill(batch, Palette.ORANGE, 120f, top(319f), 228f, 42f);
        assets.fill(batch, Palette.INK, 94f, top(413f), 254f, 42f);
        assets.fill(batch, Palette.BOARD_ROW, 0f, 0f, GameConfig.VIEW_WIDTH, 52f);
    }

    private void drawHeading(SpriteBatch batch, Assets assets) {
        BitmapFont font = assets.getFont();
        BitmapFont small = assets.getSmallFont();
        BitmapFont big = assets.getBigFont();

        small.setColor(Palette.ORANGE);
        small.draw(batch, "UN JEU DE RAGE EN 2D", 72f, top(60f));

        big.setColor(Palette.ORANGE);
        big.draw(batch, "TRIP", 77f, top(83f) - 5f);
        big.draw(batch, "TRAP", 77f, top(153f) - 5f);
        big.setColor(Palette.CREAM);
        big.draw(batch, "TRIP", 72f, top(78f));
        big.draw(batch, "TRAP", 72f, top(148f));

        font.setColor(Palette.BEIGE);
        font.draw(batch, "Huit pays. Un chrono.\nAucune pitié.",
                72f, top(taglineTopFromTop()), 330f, Align.left, true);
        font.setColor(Palette.INK);
        font.draw(batch, "Embarquer", 120f, top(279f));
        small.setColor(Palette.INK);
        small.draw(batch, "Entrée", 258f, top(286f));
        font.setColor(Palette.CREAM);
        font.draw(batch, "Commandes", 94f, top(333f));
        small.setColor(Palette.MUTED);
        small.draw(batch, "Flèches : courir · Haut ou Espace : sauter · Échap : quitter",
                72f, top(378f));

        small.setColor(Palette.BEIGE);
        assets.drawTinted(batch, "icon_clock", Palette.BEIGE, 744f, top(22f));
        small.draw(batch, "Chrono par pays : 03:00", 766f, top(22f));
        font.setColor(Color.WHITE);
        small.setColor(Color.WHITE);
        big.setColor(Color.WHITE);
    }

    static float taglineTopFromTop() {
        return MENU_BUTTON_TOP_FROM_TOP - TAGLINE_BLOCK_HEIGHT - TAGLINE_BUTTON_GAP;
    }

    private void drawDepartureBoard(SpriteBatch batch, Assets assets) {
        BitmapFont font = assets.getFont();
        BitmapFont small = assets.getSmallFont();

        font.setColor(Palette.GOLD);
        font.draw(batch, "D É P A R T S", 436f, top(282f));
        small.setColor(Palette.MUTED);
        small.draw(batch, "Porte d'embarquement : la mort", 700f, top(286f),
                212f, Align.right, false);
        small.draw(batch, "VOL", 436f, top(306f));
        small.draw(batch, "DESTINATION", 496f, top(306f));
        small.draw(batch, "PORTE", 620f, top(306f));
        small.draw(batch, "STATUT", 672f, top(306f));
        small.draw(batch, "DIFFICULTÉ", 792f, top(306f));

        for (int index = 0; index < countries.length; index++) {
            float rowTop = 322f + index * 20f;
            if (index == selected) {
                assets.fill(batch, Palette.BOARD_ROW, TABLE_X, top(rowTop + 18f), TABLE_WIDTH, 20f);
                font.setColor(Palette.ORANGE);
                font.draw(batch, ">", 426f, top(rowTop));
            }

            int bombs = countries[index].getDifficulty();
            font.setColor(Palette.GOLD);
            small.setColor(Palette.GOLD);
            small.draw(batch, String.format(Locale.ROOT, "TT %02d", index + 1), 436f, top(rowTop));
            small.draw(batch, countries[index].getDisplayName().toUpperCase(Locale.ROOT),
                    496f, top(rowTop));
            small.draw(batch, GATES.charAt(index) + String.valueOf(index + 1), 620f, top(rowTop));
            small.setColor(statusColor(bombs));
            small.draw(batch, statusText(bombs), 672f, top(rowTop));

            if (assets.hasTexture("icon_bomb")) {
                Texture bomb = assets.getTexture("icon_bomb");
                for (int count = 0; count < bombs; count++) {
                    batch.draw(bomb, BOMB_X + count * BOMB_STEP, top(rowTop + 14f),
                            BOMB_SIZE, BOMB_SIZE);
                }
            }
        }

        font.setColor(Color.WHITE);
        small.setColor(Color.WHITE);
    }

    private void drawCredits(SpriteBatch batch, Assets assets) {
        BitmapFont small = assets.getSmallFont();
        small.setColor(Palette.BEIGE);
        small.draw(batch, "Najet · Teikinui · Julien — Epitech 2026",
                600f, top(520f), 330f, Align.right, false);
        small.setColor(Color.WHITE);
    }

    /** Bombes dessinées en formes simples si icon_bomb.png n'est pas livré. */
    private void drawFallbackBombs(Assets assets) {
        if (assets.hasTexture("icon_bomb")) {
            return;
        }
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int index = 0; index < countries.length; index++) {
            float rowTop = 322f + index * 20f;
            float centerY = top(rowTop + 8f);
            for (int count = 0; count < countries[index].getDifficulty(); count++) {
                float x = BOMB_X + count * BOMB_STEP;
                shapes.setColor(Palette.ORANGE_DARK);
                shapes.circle(x + BOMB_SIZE / 2f, centerY, BOMB_SIZE / 2f + 1f);
                shapes.setColor(Palette.ORANGE);
                shapes.circle(x + BOMB_SIZE / 2f, centerY, BOMB_SIZE / 2f - 1f);
                shapes.rect(x + 7f, centerY + 4f, 3f, 4f);
            }
        }
        shapes.end();
    }

    static String statusText(int bombs) {
        return switch (bombs) {
            case 1 -> "À L'HEURE";
            case 2 -> "RETARDÉ";
            case 3 -> "ANNULÉ ?";
            default -> "PERDU";
        };
    }

    static Color statusColor(int bombs) {
        return switch (bombs) {
            case 1 -> Palette.ON_TIME;
            case 2 -> Palette.LATE;
            case 3 -> Palette.CANCELLED;
            default -> Palette.LOST;
        };
    }

    @Override
    public void dispose() {
        shapes.dispose();
        super.dispose();
    }
}