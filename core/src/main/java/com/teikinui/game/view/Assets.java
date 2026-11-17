package com.teikinui.game.view;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Charge chaque ressource une seule fois et la retrouve par son nom.
 * Toutes les ressources sont libérées ensemble dans dispose().
 */
public final class Assets implements Disposable {
    private static final float TEXT_SCALE = 1.4f;
    private static final float TITLE_SCALE = 3f;
    private static final float BIG_SCALE = 5f;
    private static final float SMALL_SCALE = 0.9f;

    private final BitmapFont font;
    private final BitmapFont titleFont;
    private final BitmapFont bigFont;
    private final BitmapFont smallFont;
    private final Texture pixel;
    private final Map<String, Texture> textures = new HashMap<>();

    public Assets() {
        font = new BitmapFont();
        font.getData().setScale(TEXT_SCALE);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(TITLE_SCALE);

        bigFont = new BitmapFont();
        bigFont.getData().setScale(BIG_SCALE);
        smallFont = new BitmapFont();
        smallFont.getData().setScale(SMALL_SCALE);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            pixel = new Texture(pixmap);
        } finally {
            pixmap.dispose();
        }
    }

    /** Police des textes normaux (HUD, messages). */
    public BitmapFont getFont() {
        return font;
    }

    /** Grande police des titres. */
    public BitmapFont getTitleFont() {
        return titleFont;
    }

    public BitmapFont getBigFont() {
        return bigFont;
    }

    public BitmapFont getSmallFont() {
        return smallFont;
    }

    /** Vrai si assets/sprites/nom.png existe; les appelants peuvent alors choisir un fallback. */
    public boolean hasTexture(String name) {
        return name != null && !name.isBlank() && Gdx.files != null
                && Gdx.files.internal("assets/sprites/" + name + ".png").exists();
    }

    /** Image assets/sprites/nom.png, chargée au premier appel puis réutilisée. */
    public Texture getTexture(String name) {
        if (!hasTexture(name)) {
            throw new IllegalArgumentException("Texture absente : assets/sprites/" + name + ".png");
        }
        return textures.computeIfAbsent(name,
                key -> new Texture(Gdx.files.internal("assets/sprites/" + key + ".png")));
    }

    /** Dessine un rectangle plein avec les coordonnées du monde (origine en bas à gauche). */
    public void fill(SpriteBatch batch, Color color, float x, float y, float width, float height) {
        batch.setColor(color);
        try {
            batch.draw(pixel, x, y, width, height);
        } finally {
            batch.setColor(Color.WHITE);
        }
    }

    /** Dessine une image blanche teintée; une image absente est simplement ignorée. */
    public void drawTinted(SpriteBatch batch, String name, Color color, float x, float y) {
        if (!hasTexture(name)) {
            return;
        }
        batch.setColor(color);
        try {
            batch.draw(getTexture(name), x, y);
        } finally {
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        bigFont.dispose();
        smallFont.dispose();
        pixel.dispose();
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
    }
}