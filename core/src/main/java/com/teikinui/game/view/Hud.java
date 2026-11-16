package com.teikinui.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameSession;
import com.teikinui.game.model.GameTimer;
import com.teikinui.game.level.LevelManager;
import com.teikinui.game.level.LevelTheme;

/** Bandeau supérieur : pays, chrono et morts, dessiné sur la rangée haute du niveau. */
public final class Hud implements Drawable {
    private final Assets assets;
    private final GameTimer timer;
    private final GameSession session;
    private final LevelManager levels;
    private LevelTheme styledTheme;
    private ThemeStyle style;

    public Hud(Assets assets, GameTimer timer, GameSession session, LevelManager levels) {
        if (assets == null || timer == null || session == null || levels == null) {
            throw new IllegalArgumentException("Les dépendances du HUD sont obligatoires.");
        }
        this.assets = assets;
        this.timer = timer;
        this.session = session;
        this.levels = levels;
        updateStyle();
    }

    @Override
    public void draw(SpriteBatch batch) {
        updateStyle();
        BitmapFont font = assets.getFont();
        float bottom = GameConfig.VIEW_HEIGHT - GameConfig.TILE;
        assets.fill(batch, style.hudBar(), 0f, bottom,
                GameConfig.VIEW_WIDTH, GameConfig.TILE);

        font.setColor(style.accent());
        font.draw(batch, "NIVEAU " + (levels.getCurrent().ordinal() + 1), 16f, bottom + 24f);
        font.setColor(style.soft());
        font.draw(batch, levels.getCurrent().getDisplayName(), 130f, bottom + 24f);

        assets.drawTinted(batch, "icon_clock", style.halo(), 420f, bottom + 8f);
        font.setColor(timer.getRemaining() <= 30f ? Palette.TIME_LOW : Palette.WHITE);
        font.draw(batch, formatTime(timer.getRemaining()), 442f, bottom + 24f);

        assets.drawTinted(batch, "icon_skull", Palette.WHITE, 880f, bottom + 8f);
        font.setColor(Palette.WHITE);
        font.draw(batch, Integer.toString(session.getDeathCount()), 902f, bottom + 24f);
        font.setColor(Color.WHITE);
    }

    private void updateStyle() {
        LevelTheme current = levels.getCurrent();
        if (current != styledTheme) {
            styledTheme = current;
            style = ThemeStyle.of(current);
        }
    }

    /** 479,2 secondes donnent « 08:00 »; 61 secondes donnent « 01:01 ». */
    public static String formatTime(float seconds) {
        int total = (int) Math.ceil(Math.max(0f, seconds));
        return String.format("%02d:%02d", total / 60, total % 60);
    }
}