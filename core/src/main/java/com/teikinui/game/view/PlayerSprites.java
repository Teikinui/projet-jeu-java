package com.teikinui.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/** Animations du voyageur, avec priorité au sprite propre au pays. */
public final class PlayerSprites {
    public enum State {
        IDLE("idle", 0.25f, Animation.PlayMode.LOOP),
        WALK("walk", 0.10f, Animation.PlayMode.LOOP),
        JUMP("jump", 0.12f, Animation.PlayMode.NORMAL),
        DIE("die", 0.10f, Animation.PlayMode.NORMAL);

        private final String fileName;
        private final float frameDuration;
        private final Animation.PlayMode playMode;

        State(String fileName, float frameDuration, Animation.PlayMode playMode) {
            this.fileName = fileName;
            this.frameDuration = frameDuration;
            this.playMode = playMode;
        }
    }

    private final Animation<TextureRegion> idle;
    private final Animation<TextureRegion> walk;
    private final Animation<TextureRegion> jump;
    private final Animation<TextureRegion> die;

    public PlayerSprites(Assets assets, String country) {
        if (assets == null || country == null || country.isBlank()) {
            throw new IllegalArgumentException("Les ressources et le nom du pays sont obligatoires.");
        }
        idle = loadFor(assets, country, State.IDLE);
        walk = loadFor(assets, country, State.WALK);
        jump = loadFor(assets, country, State.JUMP);
        die = loadFor(assets, country, State.DIE);
    }

    private static Animation<TextureRegion> loadFor(Assets assets, String country, State state) {
        Animation<TextureRegion> countryAnimation = load(assets,
                "player_" + country + "_" + state.fileName, state);
        Animation<TextureRegion> baseAnimation = load(assets, "player_" + state.fileName, state);
        return preferCountryAnimation(countryAnimation, baseAnimation);
    }

    private static Animation<TextureRegion> load(Assets assets, String prefix, State state) {
        Array<TextureRegion> frames = new Array<>();
        for (int index = 0; assets.hasTexture(prefix + "_" + index); index++) {
            frames.add(new TextureRegion(assets.getTexture(prefix + "_" + index)));
        }
        if (frames.isEmpty() && assets.hasTexture(prefix)) {
            Texture strip = assets.getTexture(prefix);
            int frameSize = strip.getHeight();
            if (frameSize > 0 && strip.getWidth() >= frameSize) {
                TextureRegion[][] regions = TextureRegion.split(strip, frameSize, frameSize);
                for (TextureRegion frame : regions[0]) {
                    frames.add(frame);
                }
            }
        }
        return frames.isEmpty() ? null
                : new Animation<>(state.frameDuration, frames, state.playMode);
    }

    static <T> Animation<T> preferCountryAnimation(Animation<T> country, Animation<T> base) {
        return country != null ? country : base;
    }

    static State stateFor(float horizontalVelocity, boolean onGround, boolean dead) {
        if (dead) {
            return State.DIE;
        }
        if (!onGround) {
            return State.JUMP;
        }
        return Math.abs(horizontalVelocity) > 0.01f ? State.WALK : State.IDLE;
    }

    public Animation<TextureRegion> getAnimation(State state) {
        return switch (state) {
            case IDLE -> idle;
            case WALK -> walk;
            case JUMP -> jump;
            case DIE -> die;
        };
    }

    public float getDuration(State state) {
        Animation<TextureRegion> animation = getAnimation(state);
        return animation == null ? 0f : animation.getAnimationDuration();
    }
}
