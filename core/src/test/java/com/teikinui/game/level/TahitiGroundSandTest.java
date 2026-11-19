package com.teikinui.game.level;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.teikinui.game.model.CollisionSystem;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.ground.QuickSand;
import com.teikinui.game.model.traps.ground.ReturnDoor;

class TahitiGroundSandTest {
    @Test
    void sandIsAStandableGroundTileThatSlowsOnItsSurfaceAndMiddleDoorIsGone() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..").resolve(path);
        Level level = LevelLoader.fromString(Files.readString(path));
        int col = 14;
        assertEquals('q', level.getTrapLetter(col, 0));
        assertEquals((char) 0, level.getTrapLetter(col, 1));
        assertTrue(level.isWall(col, 0));
        assertEquals((char) 0, level.getTrapLetter(10, 7));
        Player player = new Player(col * Level.TILE, Level.TILE, 32, 32, GameConfig.RUN_SPEED);
        var traps = level.createTraps(player);
        assertEquals(6, traps.stream().filter(QuickSand.class::isInstance).count());
        assertEquals(0, traps.stream().filter(ReturnDoor.class::isInstance).count());
        var sand = traps.stream().filter(t -> t instanceof QuickSand
                && t.getX() == col * Level.TILE && t.getY() == 0).findFirst().orElseThrow();
        player.update(1f / 30f, false, false, false);
        new CollisionSystem().move(player, level, 1f / 30f);
        assertEquals(Level.TILE, player.getY(), 0.001f);
        sand.update(1f / 30f);
        sand.onPlayerContact(player, new EventBus());
        player.update(0f, false, true, false);
        assertEquals(GameConfig.RUN_SPEED / 2f, player.getVx(), 0.001f);
        player.teleport((col + 2) * Level.TILE, Level.TILE);
        sand.onPlayerContact(player, new EventBus());
        player.update(0f, false, true, false);
        assertEquals(GameConfig.RUN_SPEED, player.getVx(), 0.001f);
    }
}
