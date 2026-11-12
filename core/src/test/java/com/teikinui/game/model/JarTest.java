package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.Trap;

class JarTest {
    private static final String MAP = String.join("\n",
            "#########",
            "#####j###",
            "#.......#",
            "#.......#",
            "#P......#",
            "#########");

    private Trap jarFor(Level level, Player player) {
        return level.createTraps(player).get(0);
    }

    private void play(Trap jar, float seconds) {
        for (float t = 0; t < seconds; t += 1f / 60f) {
            jar.update(1f / 60f);
        }
    }

    @Test
    void theJarIsPartOfTheCeilingAtTheStart() {
        Level level = LevelLoader.fromString(MAP);
        jarFor(level, new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED));
        assertTrue(level.isWall(5, 4));
    }

    @Test
    void theJarFallsFastWhenThePlayerIsRightNextToItAndBecomesAStep() {
        Level level = LevelLoader.fromString(MAP);
        Player player = new Player(3 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        Trap jar = jarFor(level, player);
        play(jar, 0.5f);
        assertEquals(Trap.State.WAITING, jar.getState());

        player.teleport(4 * 32f - 10f, 32f);
        play(jar, 0.25f);
        assertFalse(level.isWall(5, 4));
        assertTrue(level.isWall(5, 1));
        assertEquals(Trap.State.DONE, jar.getState());
    }

    @Test
    void theJarKillsThePlayerUnderIt() {
        Level level = LevelLoader.fromString(MAP);
        Player player = new Player(5 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        Trap jar = jarFor(level, player);
        EventBus bus = new EventBus();
        List<GameEvent> received = new ArrayList<>();
        bus.subscribe(received::add);
        for (int i = 0; i < 60 && received.isEmpty(); i++) {
            jar.update(1f / 60f);
            jar.onPlayerContact(player, bus);
        }
        assertEquals(List.of(GameEvent.PLAYER_DIED), received);
        assertFalse(level.isWall(5, 1));
    }

    @Test
    void afterADeathTheJarWaitsForThePlayerToComeCloser() {
        Player player = new Player(4 * 32f - 12f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        Level firstTry = LevelLoader.fromString(MAP);
        Trap jar = jarFor(firstTry, player);
        jar.update(1f / 60f);
        assertEquals(Trap.State.TRIGGERED, jar.getState());

        Level secondTry = LevelLoader.fromString(MAP);
        secondTry.setAttempt(1);
        Trap learningJar = jarFor(secondTry, player);
        learningJar.update(1f / 60f);
        assertEquals(Trap.State.WAITING, learningJar.getState());
    }
}