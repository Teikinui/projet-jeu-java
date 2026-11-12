package com.teikinui.game.level;

import com.teikinui.game.model.Level;
import com.teikinui.game.model.Tile;
import com.teikinui.game.model.traps.factory.TrapFactory;

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
        char[][] traps = new char[rows][cols];
        for (int rowFromTop = 0; rowFromTop < rows; rowFromTop++) {
            if (lines[rowFromTop].length() != cols) {
                throw new IllegalArgumentException("Toutes les lignes du niveau doivent avoir la même largeur.");
            }
            int rowFromBottom = rows - 1 - rowFromTop;
            for (int col = 0; col < cols; col++) {
                char symbol = lines[rowFromTop].charAt(col);
                grid[rowFromBottom][col] = Tile.fromChar(symbol);
                if (TrapFactory.isTrap(symbol)) traps[rowFromBottom][col] = symbol;
            }
        }

        Level level = new Level(grid);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (traps[row][col] != 0) level.setTrapLetter(col, row, traps[row][col]);
            }
        }
        return level;
    }
}
