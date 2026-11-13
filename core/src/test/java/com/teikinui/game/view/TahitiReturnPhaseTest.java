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
        List<Trap> prepared = TahitiTrapPlacement.returnWave(level, player);
        Goal goal = level.createGoal();
        assertFalse(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, level, traps, prepared));
        assertEquals(before, traps.size());
        player.teleport(goal.getX() - 2 * Level.TILE, goal.getY());
        assertTrue(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, level, traps, prepared));
        assertEquals(before + 3, traps.size());
        assertEquals(2, traps.subList(before, traps.size()).stream().filter(CocoFall.class::isInstance).count());
        assertEquals(1, traps.subList(before, traps.size()).stream().filter(Starfish.class::isInstance).count());
        assertFalse(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, level, traps, prepared));
        assertEquals(before + 3, traps.size());

        Level retry = LevelLoader.fromString(map);
        Player respawn = new Player(retry.getStartX(), retry.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> fresh = TahitiTrapPlacement.instantiate(retry, respawn, retry.createTraps(respawn), plan);
        assertEquals(before, fresh.size());
        Goal resetGoal = retry.createGoal();
        List<Trap> freshPrepared = TahitiTrapPlacement.returnWave(retry, respawn);
        respawn.teleport(resetGoal.getX() - 2 * Level.TILE, resetGoal.getY());
        assertTrue(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, respawn, resetGoal, retry, fresh, freshPrepared));
        assertEquals(before + 3, fresh.size());
        for (int i = 0; i < fresh.size(); i++) {
            assertEquals(traps.get(i).getClass(), fresh.get(i).getClass());
            assertEquals(traps.get(i).getX(), fresh.get(i).getX());
            assertEquals(traps.get(i).getY(), fresh.get(i).getY());
            assertNotSame(traps.get(i), fresh.get(i));
        }
    }

    @Test void consumedMiddleCoconutIsRecreatedDormantAtSamePositionOnRespawn() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        String map = Files.readString(path);
        Level first = LevelLoader.fromString(map);
        Player player = new Player(first.getStartX(), first.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> active = first.createTraps(player);
        List<Trap> wave = TahitiTrapPlacement.returnWave(first, player);
        Goal goal = first.createGoal();
        assertFalse(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, first, active, wave));
        assertTrue(active.stream().noneMatch(wave::contains));
        PlayScreen.updateTraps(LevelTheme.TAHITI, first, goal, active, 0.25f);
        assertTrue(wave.stream().allMatch(t -> t.getState() == Trap.State.WAITING));
        player.teleport(goal.getX() - 2 * Level.TILE, goal.getY());
        assertTrue(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, player, goal, first, active, wave));
        Trap middle = wave.get(1);
        float middleStartY = middle.getY();
        player.teleport(middle.getX(), middle.getY() - Level.TILE);
        middle.update(0.25f);
        middle.onPlayerContact(player, new com.teikinui.game.model.EventBus());
        assertEquals(Trap.State.DONE, middle.getState());

        // Mêmes étapes que loadLevel() après la mort : carte et objets neufs, vague en réserve.
        Level retry = LevelLoader.fromString(map);
        Player respawn = new Player(retry.getStartX(), retry.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> fresh = retry.createTraps(respawn);
        List<Trap> freshWave = TahitiTrapPlacement.returnWave(retry, respawn);
        assertEquals(3, freshWave.size());
        assertTrue(fresh.stream().noneMatch(freshWave::contains));
        for (int i = 0; i < 3; i++) {
            assertNotSame(wave.get(i), freshWave.get(i));
            assertEquals(wave.get(i).getClass(), freshWave.get(i).getClass());
            assertEquals(i == 1 ? 12 * Level.TILE : wave.get(i).getX(), freshWave.get(i).getX());
            assertEquals(i == 1 ? middleStartY : wave.get(i).getY(), freshWave.get(i).getY());
            assertEquals(Trap.State.WAITING, freshWave.get(i).getState());
        }
        Goal reset = retry.createGoal();
        assertFalse(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, respawn, reset, retry, fresh, freshWave));
        assertTrue(fresh.stream().noneMatch(freshWave::contains));
        respawn.teleport(reset.getX() - 2 * Level.TILE, reset.getY());
        assertTrue(PlayScreen.activateTahitiReturnWave(LevelTheme.TAHITI, respawn, reset, retry, fresh, freshWave));
        assertEquals(3, fresh.stream().filter(freshWave::contains).count());
        respawn.teleport(freshWave.get(1).getX(), freshWave.get(1).getY() - Level.TILE);
        PlayScreen.updateTraps(LevelTheme.TAHITI, retry, reset, fresh, 0f);
        assertEquals(Trap.State.TRIGGERED, freshWave.get(1).getState());
    }
}
