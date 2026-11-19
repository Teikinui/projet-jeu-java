package com.teikinui.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.traps.SkyFall;

/** Panneau au centre de l'écran : le décompte du rideau qui tombe (Chine). */
public final class SkyCountdown implements Drawable {
    private static final float WIDTH = 520f;
    private static final float HEIGHT = 150f;
    private static final float BORDER = 3f;
    static final String MESSAGE = "Le rideau tombe, ne bouge plus !";

    private final Assets assets;
    private final GlyphLayout layout = new GlyphLayout();
    private ThemeStyle style;
    private SkyFall sky;

    public SkyCountdown(Assets assets) {
        if (assets == null) {
            throw new IllegalArgumentException("Les ressources du jeu sont obligatoires.");
        }
        this.assets = assets;
    }

    /** Le rideau à surveiller dans ce pays (null si le pays n'en a pas). */
    public void watch(SkyFall newSky, ThemeStyle newStyle) {
        sky = newSky;
        style = newStyle;
    }

    /** Le panneau ne s'affiche que pendant le décompte, avant la chute. */
    static boolean shows(SkyFall sky) {
        return sky != null && sky.getCountdown() > 0;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (style == null || !shows(sky)) {
            return;
        }
        float x = (GameConfig.VIEW_WIDTH - WIDTH) / 2f;
        float y = (GameConfig.VIEW_HEIGHT - HEIGHT) / 2f;
        assets.fill(batch, style.accent(), x - BORDER, y - BORDER, WIDTH + 2f * BORDER, HEIGHT + 2f * BORDER);
        assets.fill(batch, style.hudBar(), x, y, WIDTH, HEIGHT);
        drawCentered(batch, assets.getFont(), MESSAGE, y + HEIGHT - 18f);
        drawCentered(batch, assets.getBigFont(), String.valueOf(sky.getCountdown()), y + 95f);
    }

    private void drawCentered(SpriteBatch batch, BitmapFont font, String text, float top) {
        layout.setText(font, text);
        font.setColor(Palette.WHITE);
        font.draw(batch, layout, (GameConfig.VIEW_WIDTH - layout.width) / 2f, top);
        font.setColor(Color.WHITE);
    }
}