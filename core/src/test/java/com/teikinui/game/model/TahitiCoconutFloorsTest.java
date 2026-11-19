package com.teikinui.game.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.falling.CocoFall;

class TahitiCoconutFloorsTest {
    private Level tahiti() throws Exception {
        Path map = Path.of("assets/tahiti.txt");
        if (!Files.exists(map)) map = Path.of("..").resolve(map);
        return LevelLoader.fromString(Files.readString(map));
    }

    @Test void secondFloorCoconutWaitsForPlayerOnSecondFloorAndStopsOnPlatform() throws Exception {
        Level level = tahiti();
        Player player = new Player(18 * Level.TILE, Level.TILE, 32, 32, GameConfig.RUN_SPEED);
        CocoFall coco = (CocoFall) level.createTraps(player).stream()
                .filter(t -> t instanceof CocoFall && t.getY() == 10 * Level.TILE).findFirst().orElseThrow();
        coco.update(1f);
        assertEquals(Trap.State.WAITING, coco.getState());
        player.teleport(18 * Level.TILE, 7 * Level.TILE);
        coco.update(1f);
        assertEquals(Trap.State.TRIGGERED, coco.getState());
        assertEquals(7 * Level.TILE, coco.getY(), 0.001f);
        coco.update(10f);
        assertEquals(7 * Level.TILE, coco.getY(), 0.001f);
    }

    @Test void thirdFloorCoconutWaitsForPlayerOnThirdFloor() throws Exception {
        Level level = tahiti();
        Player player = new Player(8 * Level.TILE, 7 * Level.TILE, 32, 32, GameConfig.RUN_SPEED);
        CocoFall coco = (CocoFall) level.createTraps(player).stream()
                .filter(t -> t instanceof CocoFall && t.getY() == 14 * Level.TILE).findFirst().orElseThrow();
        coco.update(1f);
        assertEquals(Trap.State.WAITING, coco.getState());
        player.teleport(8 * Level.TILE, 12 * Level.TILE);
        coco.update(1f);
        assertEquals(Trap.State.TRIGGERED, coco.getState());
        assertEquals(12 * Level.TILE, coco.getY(), 0.001f);
    }
}
