package com.teikinui.game.model;

import com.teikinui.game.level.LevelLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LevelLoaderTest {

    @Test
    void levelLoadsAndDetectsWalls() {
        String miniLevel = "###\n#P#\n###";
        Level level = LevelLoader.fromString(miniLevel);
        assertTrue(level.isWall(0, 0), "La case (0,0) devrait être un mur");
        assertTrue(level.isWall(2, 2), "La case (2,2) devrait être un mur");
    }
}
