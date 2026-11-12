package com.teikinui.game.view;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

/**
 * Charge chaque ressource une seule fois et la retrouve par son nom.
 * Toutes les ressources sont libérées ensemble dans dispose().
 */
public final class Assets implements Disposable {
    private static final float TEXT_SCALE = 1.4f;
    private static final float TITLE_SCALE = 3f;

    private final BitmapFont font;
    private final BitmapFont titleFont;
    private final Map<String, Texture> textures = new HashMap<>();

    public Assets() {
        font = new BitmapFont();
        font.getData().setScale(TEXT_SCALE);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(TITLE_SCALE);
    }

    /** Police des textes normaux (HUD, messages). */
    public BitmapFont getFont() {
        return font;
    }

    /** Grande police des titres. */
    public BitmapFont getTitleFont() {
        return titleFont;
    }

    /** Image assets/sprites/nom.png, chargée au premier appel puis réutilisée. */
    public Texture getTexture(String name) {
        return textures.computeIfAbsent(name,
                key -> new Texture(Gdx.files.internal("assets/sprites/" + key + ".png")));
    }

    @Override
    public void dispose() {
        font.dispose();
        titleFont.dispose();
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
    }
}