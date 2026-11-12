package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import com.teikinui.game.level.TrapFactory;
import com.teikinui.game.model.traps.Trap;

/** Grille d'un niveau. Les coordonnées commencent en bas à gauche. */
public class Level {
    public static final int TILE = 32;

    private final Tile[][] grid;
    private final char[][] trapLetters;

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
        this.trapLetters = new char[grid.length][cols];
    }

    /** Enregistre une lettre de piège lors du chargement de la carte texte. */
    public void setTrapLetter(int col, int row, char letter) {
        if (!TrapFactory.isTrap(letter)) {
            throw new IllegalArgumentException("Piège inconnu : " + letter);
        }
        trapLetters[row][col] = letter;
    }

    public char getTrapLetter(int col, int row) {
        return trapLetters[row][col];
    }

    public float getStartX() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                if (grid[row][col] == Tile.START) return col * TILE;
            }
        }
        return TILE;
    }

    public float getStartY() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                if (grid[row][col] == Tile.START) return row * TILE;
            }
        }
        return 2 * TILE;
    }

        /** Crée l'objectif du niveau sur la case G de la carte texte. */
    public Goal createGoal() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                if (grid[row][col] == Tile.GOAL) {
                    return new Goal(col * TILE, row * TILE);
                }
            }
        }
        throw new IllegalStateException("Le niveau n'a pas d'objectif : il manque la lettre G.");
    }

    /** Instancie les pièges sur les cases de la carte, après la création du joueur. */
    public List<Trap> createTraps(Player player) {
        List<Trap> traps = new ArrayList<>();
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                char letter = trapLetters[row][col];
                if (letter != 0) {
                    Trap trap = TrapFactory.create(letter, col * TILE, row * TILE, player);
                    trap.setStart(getStartX(), getStartY());
                    trap.setLevel(this);
                    traps.add(trap);
                }
            }
        }
        return traps;
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
        if (row < 0) {
            return false;
        }
        if (col < 0 || col >= getCols() || row >= getRows()) {
            return true;
        }
        return grid[row][col] == Tile.WALL;
    }

    /** Ajoute (true) ou enlève (false) un mur pendant la partie. */
    public void setWall(int col, int row, boolean wall) {
        if (col < 0 || col >= getCols() || row < 0 || row >= getRows()) {
            return;
        }
        grid[row][col] = wall ? Tile.WALL : Tile.EMPTY;
    }
}
