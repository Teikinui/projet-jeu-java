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

    public static List<Trap> addThree(Level level, Player player, List<Trap> existing, RandomGenerator random) {
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
                        || occupied(existing, col, row)) continue;
                candidates.add(new int[] {col, row});
            }
        }
        if (candidates.size() < 3) throw new IllegalStateException("Pas assez de cases libres à Tahiti");
        List<Trap> result = new ArrayList<>(existing);
        for (int i = 0; i < 3; i++) {
            int[] cell = candidates.remove(random.nextInt(candidates.size()));
            Trap trap = TrapFactory.create(TYPES[random.nextInt(TYPES.length)],
                    cell[0] * Level.TILE, cell[1] * Level.TILE, player);
            trap.setStart(level.getStartX(), level.getStartY());
            trap.setLevel(level);
            result.add(trap);
        }
        return result;
    }

    private static boolean occupied(List<Trap> traps, int col, int row) {
        for (Trap trap : traps) {
            if ((int)(trap.getX() / Level.TILE) == col && (int)(trap.getY() / Level.TILE) == row) return true;
        }
        return false;
    }
}
