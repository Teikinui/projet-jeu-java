package com.teikinui.game.model;

import com.teikinui.game.level.LevelLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelGoalTest {
    @Test
    void theLetterGBecomesTheGoal() {
        Level level = LevelLoader.fromString("#####\n#P.G#\n#####");
        Goal goal = level.createGoal();
        assertEquals(3 * Level.TILE, goal.getX());
        assertEquals(Level.TILE, goal.getY());
        assertFalse(level.isWall(3, 1));
    }

    @Test
    void aLevelWithoutGoalIsRefused() {
        Level level = LevelLoader.fromString("#####\n#P..#\n#####");
        assertThrows(IllegalStateException.class, level::createGoal);
    }
}