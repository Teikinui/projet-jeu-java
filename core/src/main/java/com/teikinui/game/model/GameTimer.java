package com.teikinui.game.model;

/** Chronomètre du jeu, en secondes, indépendant du rendu. */
public final class GameTimer implements Updatable {
    private float remaining = GameConfig.GAME_DURATION;

    @Override
    public void update(float dt) {
        if (!Float.isFinite(dt) || dt < 0f) {
            throw new IllegalArgumentException("Le temps écoulé doit être fini et positif.");
        }
        remaining = Math.max(0f, remaining - dt);
    }

    public float getRemaining() {
        return remaining;
    }

    /** Remet le chrono à 3 minutes (nouveau pays). */
    public void reset() {
        remaining = GameConfig.GAME_DURATION;
    }

    public boolean isTimeUp() {
        return remaining <= 0f;
    }
}