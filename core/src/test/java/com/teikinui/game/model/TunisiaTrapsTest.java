package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.Cactus;
import com.teikinui.game.model.traps.DoorSlam;
import com.teikinui.game.model.traps.Jar;
import com.teikinui.game.model.traps.Trap;

class TunisiaTrapsTest {
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();

    TunisiaTrapsTest() {
        bus.subscribe(received::add);
    }

    private static Player player(float col, float row) {
        return new Player(col * 32f, row * 32f, 32f, 32f, GameConfig.RUN_SPEED);
    }

    private static Trap first(List<Trap> traps, Class<?> type) {
        for (Trap trap : traps) {
            if (type.isInstance(trap)) {
                return trap;
            }
        }
        throw new AssertionError("piège absent : " + type.getSimpleName());
    }

    private void play(List<Trap> traps, Player player, float seconds) {
        for (float t = 0; t < seconds; t += 1f / 60f) {
            for (Trap trap : traps) {
                trap.update(1f / 60f);
                trap.onPlayerContact(player, bus);
            }
        }
    }

    @Test
    void twoJarsSideBySideFallTogether() {
        Level level = LevelLoader.fromString(String.join("\n",
                "##########",
                "######jj##",
                "#........#",
                "#........#",
                "#P.......#",
                "##########"));
        Player beside = new Player(5 * 32f - 10f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(beside);
        play(traps, beside, 1f);
        assertTrue(level.isWall(6, 1));
        assertTrue(level.isWall(7, 1));
        assertFalse(level.isWall(6, 4));
        assertFalse(level.isWall(7, 4));
        assertTrue(received.isEmpty());
    }

    @Test
    void theShaftBlockWaitsUntilThePlayerHasLanded() {
        Level level = LevelLoader.fromString(String.join("\n",
                "######",
                "#P.###",
                "#oo###",
                "#....#",
                "#....#",
                "######"));
        Player falling = player(1, 3);
        List<Trap> traps = level.createTraps(falling);
        play(traps, falling, 0.5f);
        assertEquals(Trap.State.WAITING, first(traps, DoorSlam.class).getState());
    }

    @Test
    void theShaftBlockCrushesAPlayerWhoStaysBelow() {
        Level level = LevelLoader.fromString(String.join("\n",
                "######",
                "#P.###",
                "#oo###",
                "#....#",
                "#....#",
                "######"));
        Player player = player(1, 1);
        CollisionSystem collisions = new CollisionSystem();
        player.update(1f / 60f, false, false, false);
        collisions.move(player, level, 1f / 60f);
        List<Trap> traps = level.createTraps(player);
        for (int i = 0; i < 120 && received.isEmpty(); i++) {
            player.update(1f / 60f, false, false, false);
            collisions.move(player, level, 1f / 60f);
            for (Trap trap : traps) {
                trap.update(1f / 60f);
                trap.onPlayerContact(player, bus);
            }
        }
        assertEquals(List.of(GameEvent.PLAYER_DIED), received);
    }

    @Test
    void theCactusIsHiddenUntilThePlayerComesNear() {
        Level level = LevelLoader.fromString("##########\n#P.....k.#\n##########");
        Player far = player(1, 1);
        List<Trap> traps = level.createTraps(far);
        Trap cactus = first(traps, Cactus.class);
        play(traps, far, 0.2f);
        assertFalse(cactus.isVisible());

        Player near = player(6, 1);
        List<Trap> nearTraps = level.createTraps(near);
        play(nearTraps, near, 0.1f);
        assertTrue(first(nearTraps, Cactus.class).isVisible());
        assertTrue(received.isEmpty());

        Player onIt = player(7, 1);
        List<Trap> onTraps = level.createTraps(onIt);
        play(onTraps, onIt, 0.1f);
        assertEquals(List.of(GameEvent.PLAYER_DIED), received);
    }

    @Test
    void jumpingHighOverTheCactusIsSafe() {
        Level level = LevelLoader.fromString("##########\n#........#\n#........#\n#P.....k.#\n##########");
        Player above = player(7, 3);
        List<Trap> traps = level.createTraps(above);
        play(traps, above, 0.2f);
        assertEquals(Trap.State.WAITING, first(traps, Cactus.class).getState());
        assertTrue(received.isEmpty());
    }

    @Test
    void jarsAreFallingBlocks() {
        Level level = LevelLoader.fromString("#####\n##j##\n#P..#\n#####");
        assertTrue(first(level.createTraps(player(1, 1)), Jar.class)
                instanceof com.teikinui.game.model.traps.FallingBlock);
    }
}