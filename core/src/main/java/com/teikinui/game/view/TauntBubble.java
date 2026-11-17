package com.teikinui.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teikinui.game.model.GameConfig;

/** Bulle temporaire qui affiche la phrase moqueuse du pays courant. */
public final class TauntBubble implements Drawable {
    private static final float TOP = 64f;
    private static final float HEIGHT = 28f;
    private static final float PADDING_X = 14f;
    private static final float BORDER = 2f;

    private final Assets assets;
    private final GlyphLayout layout = new GlyphLayout();
    private ThemeStyle style;
    private float timeLeft;

    public TauntBubble(Assets assets) {
        if (assets == null) {
            throw new IllegalArgumentException("Les ressources du jeu sont obligatoires.");
        }
        this.assets = assets;
    }

    public void show(ThemeStyle newStyle, float seconds) {
        if (newStyle == null || !Float.isFinite(seconds) || seconds < 0f) {
            throw new IllegalArgumentException("Le style et une durée positive sont obligatoires.");
        }
        style = newStyle;
        timeLeft = seconds;
        layout.setText(assets.getFont(), style.taunt());
    }

    public void hide() {
        timeLeft = 0f;
    }

    public void update(float deltaTime) {
        timeLeft = remainingAfter(timeLeft, deltaTime);
    }

    boolean isVisible() {
        return style != null && timeLeft > 0f;
    }

    static float remainingAfter(float timeLeft, float deltaTime) {
        if (!Float.isFinite(timeLeft) || timeLeft < 0f
                || !Float.isFinite(deltaTime) || deltaTime < 0f) {
            throw new IllegalArgumentException("La durée et le pas de temps doivent être finis et positifs.");
        }
        return Math.max(0f, timeLeft - deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!isVisible()) {
            return;
        }

        float width = layout.width + 2f * PADDING_X;
        float x = (GameConfig.VIEW_WIDTH - width) / 2f;
        float y = GameConfig.VIEW_HEIGHT - TOP - HEIGHT;
        assets.fill(batch, style.accent(), x - BORDER, y - BORDER,
                width + 2f * BORDER, HEIGHT + 2f * BORDER);
        assets.fill(batch, style.hudBar(), x, y, width, HEIGHT);

        BitmapFont font = assets.getFont();
        font.setColor(Palette.WHITE);
        font.draw(batch, layout, x + PADDING_X, y + 20f);
        font.setColor(Color.WHITE);
    }
}
