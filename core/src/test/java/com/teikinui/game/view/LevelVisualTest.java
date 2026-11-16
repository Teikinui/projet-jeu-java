package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.Level;

class LevelVisualTest {
    @Test
    void floorOnlyOnWallsWithEmptySpaceAboveAndNeverOnCeiling() {
        Level level = LevelLoader.fromString("#####\n# P #\n#####\n# G #\n#####");
        assertFalse(PlayScreen.isFloorSurface(level, 0, level.getRows() - 1));
        assertTrue(PlayScreen.isFloorSurface(level, 1, 2));
        assertTrue(PlayScreen.isFloorSurface(level, 1, 0));
        assertFalse(PlayScreen.isFloorSurface(level, 1, 1));
    }
}
