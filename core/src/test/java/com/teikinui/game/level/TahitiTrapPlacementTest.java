package com.teikinui.game.level;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.Tile;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.falling.CocoFall;
import com.teikinui.game.model.traps.ground.QuickSand;
import com.teikinui.game.model.traps.projectile.Starfish;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TahitiTrapPlacementTest {
    private Level tahiti() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        return LevelLoader.fromString(Files.readString(path));
    }

    @Test void threeUniqueRandomTrapsAvoidSpawnGoalExistingTrapsAndReservedSpikes() throws Exception {
        Level level = tahiti();
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> traps = TahitiTrapPlacement.addThree(level, player, level.createTraps(player), new Random(123));
        assertEquals(level.createTraps(player).size() + 3, traps.size());
        for (Trap trap : traps.subList(traps.size() - 3, traps.size())) {
            int col = (int) trap.getX() / GameConfig.TILE;
            int row = (int) trap.getY() / GameConfig.TILE;
            assertTrue(trap instanceof CocoFall || trap instanceof Starfish || trap instanceof QuickSand);
            assertEquals(Tile.EMPTY, level.getTile(col, row));
            assertEquals(0, level.getTrapLetter(col, row));
            assertFalse(col >= 25 && col <= 27 && row == 12);
            assertNotEquals(level.getStartX(), trap.getX());
            assertNotEquals(level.createGoal().getX(), trap.getX());
            assertTrue(level.isWall(col, row - 1), "Piège sur case accessible au-dessus du sol");
        }
        assertEquals(3, traps.subList(traps.size() - 3, traps.size()).stream()
                .map(t -> (int)t.getX() + ":" + (int)t.getY()).distinct().count());
    }

    @Test void seedReproducesPlacementAndNewSeedRerolls() throws Exception {
        Level level = tahiti();
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        String first = signature(TahitiTrapPlacement.addThree(level, player, level.createTraps(player), new Random(1)));
        assertEquals(first, signature(TahitiTrapPlacement.addThree(level, player, level.createTraps(player), new Random(1))));
        assertNotEquals(first, signature(TahitiTrapPlacement.addThree(level, player, level.createTraps(player), new Random(2))));
        Random sessionRandom = new Random(42);
        String attemptZero = signature(TahitiTrapPlacement.addThree(tahiti(), player,
                tahiti().createTraps(player), sessionRandom));
        String attemptOne = signature(TahitiTrapPlacement.addThree(tahiti(), player,
                tahiti().createTraps(player), sessionRandom));
        assertNotEquals(attemptZero, attemptOne);
    }

    private String signature(List<Trap> traps) {
        return traps.subList(traps.size() - 3, traps.size()).stream()
                .map(t -> t.getClass().getSimpleName() + ":" + t.getX() + ":" + t.getY()).toList().toString();
    }
}
