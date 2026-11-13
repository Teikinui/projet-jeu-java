package com.teikinui.game.view;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.level.TahitiTrapPlacement;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.falling.CocoFall;
import com.teikinui.game.model.traps.projectile.Starfish;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TahitiReturnPhaseTest {
    @Test void returnWaveAppearsOnlyWhenGoalMovesAndReappearsAfterRespawn() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        String map = Files.readString(path);
        Level level = LevelLoader.fromString(map);
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        var plan = TahitiTrapPlacement.plan(level, level.createTraps(player), new Random(42));
        List<Trap> traps = TahitiTrapPlacement.instantiate(level, player, level.createTraps(player), plan);
        int before = traps.size();
        Goal goal = level.createGoal();
        assertFalse(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, level, traps));
        assertEquals(before, traps.size());
        player.teleport(goal.getX() - 2 * Level.TILE, goal.getY());
        assertTrue(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, level, traps));
        assertEquals(before + 3, traps.size());
        assertEquals(2, traps.subList(before, traps.size()).stream().filter(CocoFall.class::isInstance).count());
        assertEquals(1, traps.subList(before, traps.size()).stream().filter(Starfish.class::isInstance).count());
        assertFalse(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, level, traps));
        assertEquals(before + 3, traps.size());

        Level retry = LevelLoader.fromString(map);
        Player respawn = new Player(retry.getStartX(), retry.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> fresh = TahitiTrapPlacement.instantiate(retry, respawn, retry.createTraps(respawn), plan);
        assertEquals(before, fresh.size());
        Goal resetGoal = retry.createGoal();
        respawn.teleport(resetGoal.getX() - 2 * Level.TILE, resetGoal.getY());
        assertTrue(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, respawn, resetGoal, retry, fresh));
        assertEquals(before + 3, fresh.size());
        for (int i = 0; i < fresh.size(); i++) {
            assertEquals(traps.get(i).getClass(), fresh.get(i).getClass());
            assertEquals(traps.get(i).getX(), fresh.get(i).getX());
            assertEquals(traps.get(i).getY(), fresh.get(i).getY());
            assertNotSame(traps.get(i), fresh.get(i));
        }
    }
}
