package com.teikinui.game.view;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.GameConfig;

/** Carte d'embarquement de fin de partie, même sans end_bg.png. */
public final class MessageScreen extends BaseScreen {
    /** Données figées à la fin de la partie (pas de référence au niveau qui sera détruit). */
    public record Board(String flight, String destination, String completed, String clock,
                        String gate, String deathsLabel) {
        public static Board forResult(LevelTheme country, int deaths, float remaining, boolean victory) {
            if (country == null || deaths < 0 || !Float.isFinite(remaining)) {
                throw new IllegalArgumentException("Résultat de partie invalide.");
            }
            int index = country.ordinal();
            return new Board(String.format(Locale.ROOT, "TT %02d", index + 1),
                    country.getDisplayName().toUpperCase(Locale.ROOT),
                    (victory ? LevelTheme.values().length : index) + " SUR " + LevelTheme.values().length,
                    Hud.formatTime(remaining), "ABCDEFGH".charAt(index) + String.valueOf(index + 1),
                    "T'es mort " + deaths + " fois.");
        }
    }

    private final String title;
    private final String details;
    private final Board board;
    private final Runnable restart;
    private final Runnable menu;
    private boolean ended;

    public MessageScreen(TripTrapGame game, String title, String details, Board board,
                         Runnable restart, Runnable menu) {
        super(game);
        this.title = title;
        this.details = details;
        this.board = board;
        this.restart = restart;
        this.menu = menu;
        setBackground(new float[] {Palette.HALL.r, Palette.HALL.g, Palette.HALL.b});
    }

    enum Action { NONE, RESTART, MENU }

    static Action actionFor(boolean ended, boolean r, boolean escape, boolean enter) {
        if (ended) return Action.NONE;
        if (r) return Action.RESTART;
        return escape || enter ? Action.MENU : Action.NONE;
    }

    @Override
    public void update(float dt) {
        if (ended) return;
        Action action = actionFor(false, Gdx.input.isKeyJustPressed(Input.Keys.R),
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE),
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER));
        if (action != Action.NONE) {
            ended = true;
            if (action == Action.RESTART) restart.run();
            else menu.run();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        Assets assets = game.getAssets();
        batch.begin();
        if (assets.hasTexture("end_bg")) {
            batch.draw(assets.getTexture("end_bg"), 0, 0, GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);
        } else {
            drawFallback(batch, assets);
        }
        BitmapFont small = assets.getSmallFont();
        BitmapFont font = assets.getFont();
        BitmapFont titleFont = assets.getTitleFont();

        small.setColor(Palette.LOST);
        small.draw(batch, "TEMPS ÉCOULÉ — " + board.clock(), 0, top(44) - 5,
                GameConfig.VIEW_WIDTH + 5, Align.center, false);
        small.setColor(Palette.CREAM);
        small.draw(batch, "TEMPS ÉCOULÉ — " + board.clock(), 0, top(44),
                GameConfig.VIEW_WIDTH, Align.center, false);
        titleFont.setColor(Palette.GOLD);
        titleFont.draw(batch, title, 5, top(64) - 5, GameConfig.VIEW_WIDTH, Align.center, false);
        titleFont.setColor(Palette.CREAM);
        titleFont.draw(batch, title, 0, top(64), GameConfig.VIEW_WIDTH, Align.center, false);
        font.setColor(Palette.BEIGE);
        font.draw(batch, details, 100, top(106), 760, Align.center, true);
        font.setColor(Palette.GOLD);
        font.draw(batch, board.deathsLabel(), 0, top(170), GameConfig.VIEW_WIDTH, Align.center, false);

        font.setColor(Palette.INK);
        font.draw(batch, "TRIP TRAP AIRLINES", 183, top(211));
        small.setColor(Palette.INK);
        small.draw(batch, "CARTE D'EMBARQUEMENT", 404, top(216), 208, Align.right, false);
        field(batch, small, font, "PASSAGER", "TOI", 183, 247);
        field(batch, small, font, "VOL", board.flight(), 333, 247);
        field(batch, small, font, "DESTINATION", board.destination(), 483, 247);
        field(batch, small, font, "PAYS FINIS", board.completed(), 183, 297);
        field(batch, small, font, "MORTS", board.deathsLabel().replaceAll("[^0-9]", ""), 333, 297);
        field(batch, small, font, "CHRONO", board.clock(), 483, 297);
        font.setColor(Palette.STAMP);
        font.draw(batch, "EMBARQUEMENT REFUSÉ", 362, top(336));
        small.setColor(Palette.PASS_LABEL);
        small.draw(batch, "PORTE", 648, top(217));
        font.setColor(Palette.INK);
        font.draw(batch, board.gate(), 648, top(230));
        small.setColor(Palette.PASS_LABEL);
        small.draw(batch, "fermée", 648, top(272));
        font.setColor(Palette.INK);
        font.draw(batch, "Reprendre un vol", 278, top(416));
        font.draw(batch, "Hall des départs", 516, top(416));
        small.setColor(Palette.BEIGE);
        small.draw(batch, "[R]", 459, top(416));
        small.draw(batch, "[Échap]", 678, top(416));
        small.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        titleFont.setColor(Color.WHITE);
        batch.end();
    }

    private void field(SpriteBatch batch, BitmapFont small, BitmapFont font,
                       String label, String value, float x, float y) {
        small.setColor(Palette.PASS_LABEL);
        small.draw(batch, label, x, top(y));
        font.setColor(Palette.INK);
        font.draw(batch, value, x, top(y + 12));
    }

    private void drawFallback(SpriteBatch batch, Assets assets) {
        assets.fill(batch, Palette.HALL, 0, 0, GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);
        assets.fill(batch, Palette.ORANGE_DARK, 163, top(369), 612, 182);
        assets.fill(batch, Palette.CREAM, 158, top(364), 612, 182);
        assets.fill(batch, Palette.ORANGE, 158, top(235), 612, 53);
        assets.fill(batch, Palette.PASS_LABEL, 630, top(364), 2, 129);
        assets.fill(batch, Palette.STAMP, 357, top(350), 263, 3);
        assets.fill(batch, Palette.STAMP, 357, top(381), 263, 3);
        assets.fill(batch, Palette.STAMP, 357, top(381), 3, 31);
        assets.fill(batch, Palette.STAMP, 617, top(381), 3, 31);
        assets.fill(batch, Palette.ORANGE, 268, top(437), 222, 39);
        assets.fill(batch, Palette.BOARD_ROW, 506, top(437), 228, 39);
    }
}
