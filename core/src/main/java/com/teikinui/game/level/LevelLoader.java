package com.teikinui.game.level;

import com.teikinui.game.model.Level;
import com.teikinui.game.model.Tile;

/** Convertit une carte texte en grille de tuiles. */
public final class LevelLoader {
    private LevelLoader() {
    }

    public static Level fromString(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Le texte du niveau ne peut pas être vide.");
        }

        // \R reconnaît LF et CRLF; strip retire les lignes vides aux extrémités.
        String[] lines = text.strip().split("\\R");
        int rows = lines.length;
        int cols = lines[0].length();
        if (cols == 0) {
            throw new IllegalArgumentException("Les lignes du niveau ne peuvent pas être vides.");
        }

        Tile[][] grid = new Tile[rows][cols];
        for (int rowFromTop = 0; rowFromTop < rows; rowFromTop++) {
            if (lines[rowFromTop].length() != cols) {
                throw new IllegalArgumentException("Toutes les lignes du niveau doivent avoir la même largeur.");
            }
            int rowFromBottom = rows - 1 - rowFromTop;
            for (int col = 0; col < cols; col++) {
                grid[rowFromBottom][col] = Tile.fromChar(lines[rowFromTop].charAt(col));
            }
        }

        return new Level(grid);
    }
}
