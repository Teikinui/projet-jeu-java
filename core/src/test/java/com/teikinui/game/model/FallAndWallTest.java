package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;

class FallAndWallTest {
    @Test
    void aWallCanBeAddedAndRemoved() {
        Level level = LevelLoader.fromString("#####\n#P..#\n#####");
        assertTrue(level.isWall(2, 0));
        level.setWall(2, 0, false);
        assertFalse(level.isWall(2, 0));
        level.setWall(2, 1, true);
        assertTrue(level.isWall(2, 1));
    }

    @Test
    void belowTheScreenIsEmptySoThePlayerCanFall() {
        Level level = LevelLoader.fromString("#####\n#P..#\n#####");
        assertFalse(level.isWall(2, -1));
        assertTrue(level.isWall(-1, 1));
    }

    @Test
    void fallingOutOfTheScreenKillsThePlayer() {
        EventBus bus = new EventBus();
        List<GameEvent> received = new ArrayList<>();
        bus.subscribe(received::add);
        CollisionSystem collisions = new CollisionSystem();
        collisions.checkFall(new Player(32f, 10f, 32f, 32f, GameConfig.RUN_SPEED), bus);
        assertTrue(received.isEmpty());
        collisions.checkFall(new Player(32f, -40f, 32f, 32f, GameConfig.RUN_SPEED), bus);
        assertEquals(List.of(GameEvent.PLAYER_DIED), received);
    }
}