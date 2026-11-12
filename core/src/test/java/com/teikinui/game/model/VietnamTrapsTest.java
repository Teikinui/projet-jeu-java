package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.CrumbleBlock;
import com.teikinui.game.model.traps.FallingBlock;
import com.teikinui.game.model.traps.GrowingSpike;
import com.teikinui.game.model.traps.PopSpike;
import com.teikinui.game.model.traps.Trap;

class VietnamTrapsTest {
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();
    private final CollisionSystem collisions = new CollisionSystem();

    VietnamTrapsTest() {
        bus.subscribe(received::add);
    }

    private static Trap first(List<Trap> traps, Class<?> type) {
        for (Trap trap : traps) {
            if (type.isInstance(trap)) {
                return trap;
            }
        }
        throw new AssertionError("piège absent : " + type.getSimpleName());
    }

    /** Fait tourner le jeu : le joueur avance (ou pas), tombe, touche les pièges. */
    private void play(Level level, List<Trap> traps, Player player, boolean left, boolean right, float seconds) {
        for (float t = 0; t < seconds; t += 1f / 60f) {
            player.update(1f / 60f, left, right, false);
            collisions.move(player, level, 1f / 60f);
            for (Trap trap : traps) {
                trap.update(1f / 60f);
            }
            collisions.checkContacts(player, traps, bus);
        }
    }

    @Test
    void theSpikeFloorKillsWhoeverFallsOnIt() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#####",
                "#P..#",
                "#...#",
                "#eee#",
                "#####"));
        Player player = new Player(32f, 3 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, false, false, 1f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void theCrumbleBlockLooksLikeAWallThenDropsIntoTheSpikes() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#######",
                "#.....#",
                "#..t..#",
                "#.....#",
                "#eeeee#",
                "#######"));
        Player player = new Player(3 * 32f, 4 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap block = first(traps, CrumbleBlock.class);
        assertTrue(level.isWall(3, 3));
        assertFalse(block.isVisible());

        play(level, traps, player, false, false, 0.2f);
        assertTrue(player.isOnGround());
        assertTrue(level.isWall(3, 3));

        play(level, traps, player, false, false, 1f);
        assertFalse(level.isWall(3, 3));
        assertFalse(level.isWall(3, 1));
        assertTrue(block instanceof FallingBlock);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void leavingTheCrumbleBlockQuicklyIsSafe() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#######",
                "#.....#",
                "#..t###",
                "#....##",
                "#eeee##",
                "#######"));
        Player player = new Player(3 * 32f, 4 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, false, false, 0.05f);
        play(level, traps, player, false, true, 0.3f);
        play(level, traps, player, false, false, 1f);
        assertTrue(received.isEmpty());
        assertFalse(level.isWall(3, 3));
    }

    @Test
    void thePopSpikeIsHiddenUntilThePlayerComesClose() {
        Level level = LevelLoader.fromString(String.join("\n",
                "##########",
                "#........#",
                "#P.....i.#",
                "##########"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap spike = first(traps, PopSpike.class);
        play(level, traps, player, false, false, 0.2f);
        assertFalse(spike.isVisible());

        play(level, traps, player, false, true, 1f);
        assertTrue(spike.isVisible());
        assertTrue(received.isEmpty());

        play(level, traps, player, false, true, 1f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void aCeilingPillarPoundsDownToTheFloorThenGoesBackUp() {
        Level level = LevelLoader.fromString(String.join("\n",
                "##########",
                "#....y...#",
                "#........#",
                "#........#",
                "#........#",
                "#P.......#",
                "##########"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        GrowingSpike pillar = (GrowingSpike) first(traps, GrowingSpike.class);
        assertTrue(pillar.isFromCeiling());

        play(level, traps, player, false, true, 0.6f);
        float lowest = 0f;
        boolean wentBackUp = false;
        boolean cameDownAgain = false;
        for (int frame = 0; frame < 4 * 60; frame++) {
            play(level, traps, player, false, false, 1f / 60f);
            lowest = Math.max(lowest, pillar.getHeight());
            if (lowest >= 5 * 32f && pillar.getHeight() == 0f) {
                wentBackUp = true;
            }
            if (wentBackUp && pillar.getHeight() > 0f) {
                cameDownAgain = true;
            }
        }
        assertEquals(5 * 32f, lowest, 0.01f);
        assertTrue(wentBackUp);
        assertTrue(cameDownAgain);
    }

    @Test
    void aCeilingPillarCrushesSomeoneStandingUnderIt() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#######",
                "#..y..#",
                "#.....#",
                "#..P..#",
                "#######"));
        Player player = new Player(3 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, false, false, 0.5f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void aFloorPillarImpalesSomeoneWhoKeepsRunning() {
        Level level = LevelLoader.fromString(String.join("\n",
                "##########",
                "#........#",
                "#........#",
                "#........#",
                "#P....y..#",
                "##########"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        GrowingSpike pillar = (GrowingSpike) first(traps, GrowingSpike.class);
        assertFalse(pillar.isFromCeiling());
        play(level, traps, player, false, true, 2f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void aPillarIgnoresThePlayerOnTheFloorBelow() {
        Level level = LevelLoader.fromString(String.join("\n",
                "########",
                "#......#",
                "#......#",
                "#...y..#",
                "########",
                "#......#",
                "#P.....#",
                "########"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, false, true, 2f);
        assertFalse(first(traps, GrowingSpike.class).isVisible());
    }
}