package com.teikinui.game.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.level.LevelManager;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameSession;
import com.teikinui.game.model.GameTimer;

/**
 * Le bandeau en haut de l'écran : le pays, le chrono et le nombre de morts.
 * Il s'affiche sur la rangée de murs du haut du niveau.
 * À appeler entre batch.begin() et batch.end().
 */
public final class Hud implements Drawable {
    private static final float MARGIN = 16f;
    private static final float TEXT_Y = GameConfig.VIEW_HEIGHT - 8f;

    private final BitmapFont font;
    private final GameTimer timer;
    private final GameSession session;
    private final LevelManager levels;

    public Hud(BitmapFont font, GameTimer timer, GameSession session, LevelManager levels) {
        this.font = font;
        this.timer = timer;
        this.session = session;
        this.levels = levels;
    }

    @Override
    public void draw(SpriteBatch batch) {
        float width = GameConfig.VIEW_WIDTH - 2 * MARGIN;
        font.draw(batch, levels.getCurrent().getDisplayName(), MARGIN, TEXT_Y, width, Align.left, false);
        font.draw(batch, formatTime(timer.getRemaining()), MARGIN, TEXT_Y, width, Align.center, false);
        font.draw(batch, "Morts : " + session.getDeathCount(), MARGIN, TEXT_Y, width, Align.right, false);
    }

    /** 479.2 secondes donnent "08:00" ; 61 secondes donnent "01:01". */
    public static String formatTime(float seconds) {
        int total = (int) Math.ceil(Math.max(0f, seconds));
        return String.format("%02d:%02d", total / 60, total % 60);
    }
}