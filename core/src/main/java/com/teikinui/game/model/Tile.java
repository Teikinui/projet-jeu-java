package com.teikinui.game.model;

public enum Tile {
    EMPTY,
    WALL,
    START;

    public static Tile fromChar(char c) {
        return switch (c){
            case '#' -> WALL;
            case 'P' -> START;
            default -> EMPTY;
        };
    }
}
