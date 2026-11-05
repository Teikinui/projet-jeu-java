package com.teikinui.game.model;

public class Level {
    public static final int TILE = 32;
    
    private final Tile [][] grid;

    public Level(Tile [][] grid){
        this.grid = grid;
    }

    public int getRows() {
        return grid.length;
    }

    public int getCols() {
        return grid[0].length;
    }

    public boolean isWall(int col, int row) {
        if (col<0 || col >=getCols()||row <0 || row>=getRows()) {
            return true;
        } 
        return grid[row][col] == Tile.WALL;
    }
}
