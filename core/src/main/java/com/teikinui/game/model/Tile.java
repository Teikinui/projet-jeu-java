package com.teikinui.game.model;

/** Type d'une case de la grille du niveau. */
public enum Tile {
    EMPTY,
    WALL,
    START;

    public static Tile fromChar(char symbol) {
        return switch (symbol) {
            case '#' -> WALL;
            case 'P' -> START;
            default -> EMPTY;
        };
    }
}
