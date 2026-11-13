package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.traps.Icicle;
import com.teikinui.game.model.traps.Snowball;
import com.teikinui.game.model.traps.Trap;

class LaplandTest {
    private static final float DT = 1f / 60f;
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();
    private final CollisionSystem collisions = new CollisionSystem();

    LaplandTest() {
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
    void onIceThePlayerSpeedsUpSlowlyAndKeepsSliding() {
        Player player = new Player(0f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        player.setIcy(true);
        player.update(DT, false, true, false);
        assertTrue(player.getVx() > 0f);
        assertTrue(player.getVx() < GameConfig.RUN_SPEED / 2f);
        for (float t = 0; t < 2f; t += DT) {
            player.update(DT, false, true, false);
        }
        player.update(DT, false, false, false);
        assertTrue(player.getVx() > GameConfig.RUN_SPEED / 2f);
    }

    @Test
    void withoutIceThePlayerStopsAtOnce() {
        Player player = new Player(0f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        player.update(DT, false, true, false);
        player.update(DT, false, false, false);
        assertEquals(0f, player.getVx(), 0.001f);
    }

    @Test
    void onlyLaplandIsIcyAndItIsTheHardest() {
        for (LevelTheme theme : LevelTheme.values()) {
            assertEquals(theme == LevelTheme.LAPONIE, theme.isIcy(), theme.name());
            assertTrue(theme.getDifficulty() >= 1 && theme.getDifficulty() <= 4, theme.name());
        }
        assertEquals(3, LevelTheme.CHINE.getDifficulty());
        assertEquals(2, LevelTheme.LAPONIE.getDifficulty());
        assertEquals(1, LevelTheme.FRANCE.getDifficulty());
    }

    @Test
    void theIcicleFallsOnSomeoneBelowAndBreaksOnTheFloor() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#######",
                "#..I..#",
                "#.....#",
                "#.....#",
                "#..P..#",
                "#######"));
        Player player = new Player(3 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap icicle = first(traps, Icicle.class);
        float hanging = icicle.getY();
        play(level, traps, player, false, 0.1f);
        assertTrue(icicle.getY() < hanging);
        play(level, traps, player, false, 1f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void theSnowballRollsTowardThePlayer() {
        Level level = LevelLoader.fromString(String.join("\n",
                "############",
                "#P........B#",
                "############"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap ball = first(traps, Snowball.class);
        float start = ball.getX();
        play(level, traps, player, false, 0.5f);
        assertTrue(ball.getX() < start);
        play(level, traps, player, false, 2f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
        assertFalse(ball.isActive());
    }
}