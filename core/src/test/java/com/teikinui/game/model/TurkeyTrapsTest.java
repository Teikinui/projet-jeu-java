package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.Dervish;
import com.teikinui.game.model.traps.SimitVendor;
import com.teikinui.game.model.traps.Trap;

class TurkeyTrapsTest {
    private static final float DT = 1f / 60f;
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();
    private final CollisionSystem collisions = new CollisionSystem();

    TurkeyTrapsTest() {
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

    private void play(Level level, List<Trap> traps, Player player, boolean right, float seconds) {
        for (float t = 0; t < seconds; t += DT) {
            player.update(DT, false, right, false);
            collisions.move(player, level, DT);
            for (Trap trap : traps) {
                trap.update(DT);
            }
            collisions.checkContacts(player, traps, bus);
        }
    }

    @Test
    void theDervishGoesBackAndForth() {
        Level level = LevelLoader.fromString(String.join("\n",
                "################",
                "#P......D......#",
                "################"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap dervish = first(traps, Dervish.class);
        float start = dervish.getX();
        float min = start;
        float max = start;
        for (float t = 0; t < 6f; t += DT) {
            dervish.update(DT);
            min = Math.min(min, dervish.getX());
            max = Math.max(max, dervish.getX());
        }
        assertTrue(max > start + 2 * 32f);
        assertTrue(min < start - 2 * 32f);
    }

    @Test
    void touchingTheDervishKills() {
        Level level = LevelLoader.fromString(String.join("\n",
                "##########",
                "#P...D...#",
                "##########"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, true, 3f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void loukoumHalvesTheSpeedButDoesNotKill() {
        Level level = LevelLoader.fromString(String.join("\n",
                "########",
                "#PLLLLL#",
                "########"));
        Player player = new Player(2 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        play(level, traps, player, true, 0.2f);
        assertTrue(player.getVx() < GameConfig.RUN_SPEED * 0.6f);
        assertTrue(received.isEmpty());
    }

    @Test
    void theSimitVendorThrowsAtThePlayerUntilHeIsHit() {
        Level level = LevelLoader.fromString(String.join("\n",
                "############",
                "#P........S#",
                "############"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        SimitVendor vendor = (SimitVendor) first(traps, SimitVendor.class);
        float trayX = vendor.getX();

        play(level, traps, player, false, 0.7f);
        assertTrue(vendor.getX() < trayX);
        assertFalse(received.contains(GameEvent.PLAYER_DIED));

        play(level, traps, player, false, 2f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }
}