package com.teikinui.game.level;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.falling.CocoFall;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TahitiMapTest {
    @Test
    void bothFallingTrapsAreCoconuts() throws IOException {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        String map = Files.readString(path);
        assertEquals(2, map.chars().filter(c -> c == 'c').count());
        assertEquals(0, map.chars().filter(c -> c == 'h').count());
        Level level = LevelLoader.fromString(map);
        Player player = new Player(level.getStartX(), level.getStartY(),
                GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE, GameConfig.RUN_SPEED);
        assertEquals(2, level.createTraps(player).stream().filter(CocoFall.class::isInstance).count());
    }
}
