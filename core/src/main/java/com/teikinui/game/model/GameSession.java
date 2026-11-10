package com.teikinui.game.model;

/** État de la partie conservé entre les niveaux. */
public final class GameSession implements GameListener {
    private int deathCount;
    private int currentLevel;

    @Override
    public void onEvent(GameEvent event) {
        if (event == GameEvent.PLAYER_DIED) {
            deathCount++;
        }
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        if (currentLevel < 0) {
            throw new IllegalArgumentException("Le niveau ne peut pas être négatif.");
        }
        this.currentLevel = currentLevel;
    }
}
