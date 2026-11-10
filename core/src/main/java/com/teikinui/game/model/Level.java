package com.teikinui.game.model;

/** Grille d'un niveau. Les coordonnées commencent en bas à gauche. */
public class Level {
    public static final int TILE = 32;

    private final Tile[][] grid;

    public Level(Tile[][] grid) {
        if (grid == null || grid.length == 0 || grid[0] == null || grid[0].length == 0) {
            throw new IllegalArgumentException("La grille du niveau ne peut pas être vide.");
        }
        int cols = grid[0].length;
        for (Tile[] row : grid) {
            if (row == null || row.length != cols) {
                throw new IllegalArgumentException("Toutes les lignes du niveau doivent avoir la même largeur.");
            }
        }
        this.grid = grid;
    }

    public int getCols() {
        return grid[0].length;
    }

    public int getRows() {
        return grid.length;
    }

    /** Retourne le type de case; les coordonnées hors grille sont des murs. */
    public Tile getTile(int col, int row) {
        if (col < 0 || col >= getCols() || row < 0 || row >= getRows()) {
            return Tile.WALL;
        }
        return grid[row][col];
    }

    /** Les coordonnées hors de la grille sont considérées comme des murs. */
    public boolean isWall(int col, int row) {
        if (col < 0 || col >= getCols() || row < 0 || row >= getRows()) {
            return true;
        }
        return grid[row][col] == Tile.WALL;
    }
}
