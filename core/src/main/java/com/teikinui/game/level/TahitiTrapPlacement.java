package com.teikinui.game.level;

import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.Tile;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.factory.TrapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/** Ajoute trois pièges de Tahiti sur des cases libres au-dessus d'un sol. */
public final class TahitiTrapPlacement {
    private static final char[] TYPES = {'c', 's', 'q'};

    private TahitiTrapPlacement() { }

    /** Plan immuable tiré une seule fois pour la partie, réutilisé à chaque essai. */
    public record Placement(char type, int col, int row) { }

    public static List<Trap> addThree(Level level, Player player, List<Trap> existing, RandomGenerator random) {
        return instantiate(level, player, existing, plan(level, existing, random));
    }

    public static List<Placement> plan(Level level, List<Trap> existing, RandomGenerator random) {
        Objects.requireNonNull(random);
        List<int[]> candidates = new ArrayList<>();
        int spawnCol = (int) (level.getStartX() / Level.TILE);
        int spawnRow = (int) (level.getStartY() / Level.TILE);
        int goalCol = (int) (level.createGoal().getX() / Level.TILE);
        int goalRow = (int) (level.createGoal().getY() / Level.TILE);
        for (int row = 1; row < level.getRows() - 1; row++) {
            for (int col = 1; col < level.getCols() - 1; col++) {
                if (level.getTile(col, row) != Tile.EMPTY || level.getTrapLetter(col, row) != 0
                        || !level.isWall(col, row - 1) || col == spawnCol && row == spawnRow
                        || col == goalCol && row == goalRow || col == goalCol && row == spawnRow
                        || row == goalRow && col >= goalCol - 3 && col < goalCol
                        || returnPosition(col, row) || occupied(existing, col, row)) continue;
                candidates.add(new int[] {col, row});
            }
        }
        if (candidates.size() < 3) throw new IllegalStateException("Pas assez de cases libres à Tahiti");
        List<Placement> plan = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int[] cell = candidates.remove(random.nextInt(candidates.size()));
            plan.add(new Placement(TYPES[random.nextInt(TYPES.length)], cell[0], cell[1]));
        }
        return List.copyOf(plan);
    }

    public static List<Trap> instantiate(Level level, Player player, List<Trap> existing, List<Placement> plan) {
        List<Trap> result = new ArrayList<>(existing);
        for (Placement placement : plan) {
            Trap trap = TrapFactory.create(placement.type(), placement.col() * Level.TILE,
                    placement.row() * Level.TILE, player);
            trap.setStart(level.getStartX(), level.getStartY());
            trap.setLevel(level);
            result.add(trap);
        }
        return result;
    }

    /** Emplacements vérifiés dans tahiti.txt : deux chutes au-dessus du retour bas et un tir vers P. */
    public static List<Trap> returnWave(Level level, Player player) {
        List<Placement> positions = List.of(new Placement('c', 23, 4),
                new Placement('c', 12, 4), new Placement('s', 7, 1));
        for (Placement p : positions) {
            if (level.isWall(p.col(), p.row()) || level.getTrapLetter(p.col(), p.row()) != 0
                    || p.col() * Level.TILE == level.getStartX() && p.row() * Level.TILE == level.getStartY()
                    || p.col() * Level.TILE == level.createGoal().getX()
                        && p.row() * Level.TILE == level.createGoal().getY()) {
                throw new IllegalStateException("Emplacement de vague Tahiti occupé");
            }
        }
        return instantiate(level, player, List.of(), positions);
    }

    private static boolean returnPosition(int col, int row) {
        return col == 23 && row == 4 || col == 12 && row == 4 || col == 7 && row == 1;
    }

    private static boolean occupied(List<Trap> traps, int col, int row) {
        for (Trap trap : traps) {
            if ((int)(trap.getX() / Level.TILE) == col && (int)(trap.getY() / Level.TILE) == row) return true;
        }
        return false;
    }
}
