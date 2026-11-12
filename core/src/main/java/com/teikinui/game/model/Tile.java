package com.teikinui.game.model;

/** Type d'une case de la grille du niveau. */
public enum Tile {
    EMPTY,
    WALL,
    START,
    GOAL;

    public static Tile fromChar(char symbol) {
        return switch (symbol) {
            case '#' -> WALL;
            case 'P' -> START;
            case 'G' -> GOAL;
            default -> EMPTY;
        };
    }
}
