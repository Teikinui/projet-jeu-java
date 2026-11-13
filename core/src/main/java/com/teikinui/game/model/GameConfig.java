package com.teikinui.game.model;

/** Every game setting, in one place. */
public final class GameConfig {
    public static final int TILE = 32;
    public static final float GRAVITY = -980f;
    public static final float JUMP_SPEED = 540f;
    public static final float RUN_SPEED = 150f;
    /** 3 minutes par pays : le chrono repart à 3:00 à chaque nouveau pays. */
    public static final float GAME_DURATION = 180f;
    public static final int LEVEL_COLS = 30;
    public static final int LEVEL_ROWS = 17;
    public static final int VIEW_WIDTH = LEVEL_COLS * TILE;
    public static final int VIEW_HEIGHT = LEVEL_ROWS * TILE;
    public static final float PLAYER_SIZE = 32f;

    private GameConfig() {
    }
}