package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.ChargingWall;
import com.teikinui.game.model.traps.HiddenSlab;
import com.teikinui.game.model.traps.SkyFall;
import com.teikinui.game.model.traps.TiltSlab;
import com.teikinui.game.model.traps.Trap;

class ChinaTrapsTest {
    private static final float DT = 1f / 60f;
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();
    private final CollisionSystem collisions = new CollisionSystem();

    ChinaTrapsTest() {
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

    private void play(Level level, List<Trap> traps, Player player, boolean left, boolean right, float seconds) {
        for (float t = 0; t < seconds; t += DT) {
            player.update(DT, left, right, false);
            collisions.move(player, level, DT);
            for (Trap trap : traps) {
                trap.update(DT);
            }
            collisions.checkContacts(player, traps, bus);
        }
    }

    private static final String ROOM = String.join("\n",
            "##########",
            "#.......a#",
            "#........#",
            "#P.......#",
            "##########");

    @Test
    void standingStillWhileTheSkyFallsIsSafe() {
        Level level = LevelLoader.fromString(ROOM);
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        SkyFall sky = (SkyFall) first(traps, SkyFall.class);
        play(level, traps, player, false, false, 2.2f);
        assertTrue(sky.isDangerous());
        // Le ciel est descendu jusqu'à la tête du joueur.
        float head = player.getY() + player.getHeight();
        assertTrue(sky.getSkyBottom() >= head);
        assertTrue(sky.getSkyBottom() <= head + 3f);
        play(level, traps, player, false, false, 3f);
        assertFalse(sky.isDangerous());
        assertTrue(received.isEmpty());
    }

    @Test
    void movingWhileTheSkyFallsKills() {
        Level level = LevelLoader.fromString(ROOM);
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, false, false, 1.5f);
        play(level, traps, player, false, true, 0.2f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void theWallChargesFromTheRightAndStopsBeforeTheFirstColumn() {
        Level level = LevelLoader.fromString(String.join("\n",
                "##############",
                "#............#",
                "#............#",
                "#P..........m#",
                "##############"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap wall = first(traps, ChargingWall.class);
        for (float t = 0; t < 1.8f; t += DT) {
            wall.update(DT);
        }
        assertTrue(wall.getX() < 3 * 32f);
        assertTrue(wall.getX() >= 2 * 32f);
        assertFalse(wall.collidesWith(player));
    }

    @Test
    void theHiddenSlabIsSolidAndAppearsWhenSteppedOn() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#####",
                "#...#",
                "#.n.#",
                "#e.e#",
                "#####"));
        Player player = new Player(2 * 32f, 3 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        HiddenSlab slab = (HiddenSlab) first(traps, HiddenSlab.class);
        assertTrue(level.isWall(2, 2));
        assertFalse(slab.isRevealed());
        play(level, traps, player, false, false, 0.3f);
        assertTrue(slab.isRevealed());
        assertTrue(player.isOnGround());
    }

    @Test
    void theFakeSlabLooksLikeFloorButYouFallThrough() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#####",
                "#...#",
                "#.P.#",
                "##x##",
                "##e##",
                "#####"));
        Player player = new Player(2 * 32f, 3 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        assertFalse(level.isWall(2, 2));
        play(level, traps, player, false, false, 1f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void theTiltSlabTipsOverAndDropsThePlayer() {
        Level level = LevelLoader.fromString(String.join("\n",
                "######",
                "#....#",
                "#.bb.#",
                "#.ee.#",
                "######"));
        Player player = new Player(2 * 32f, 3 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        assertTrue(level.isWall(2, 2) && level.isWall(3, 2));
        play(level, traps, player, false, false, 1.5f);
        assertFalse(level.isWall(2, 2));
        assertFalse(level.isWall(3, 2));
        assertTrue(((TiltSlab) first(traps, TiltSlab.class)).getTilt() > 0f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }
}