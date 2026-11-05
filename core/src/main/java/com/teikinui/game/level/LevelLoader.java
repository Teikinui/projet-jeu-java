package com.teikinui.game.level;

import com.teikinui.game.model.Level;
import com.teikinui.game.model.Tile;

public class LevelLoader {
    
    public static Level fromString(String text){
        String[] lines = text.split("\n");
        int rows = lines.length;
        int cols = lines[0].length();
        Tile [][] grid = new Tile[rows][cols];
        for (int i = 0;   i < rows;   i++) {
            int row = rows - 1 - i;
            for (int col = 0; col< cols; col++){
                char c = lines[i].charAt(col);
                grid[row][col] = Tile.fromChar(c);
            }
        }
        return new Level(grid);
    }
}
