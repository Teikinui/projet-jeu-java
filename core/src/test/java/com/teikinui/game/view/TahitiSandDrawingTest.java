package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.factory.TrapFactory;

class TahitiSandDrawingTest {
    @Test
    void fixedAndExtraSandBothDrawInsideTheirGroundTile() throws Exception {
        Path map = Path.of("assets/tahiti.txt");
        if (!Files.exists(map)) map = Path.of("..").resolve(map);
        Level level = LevelLoader.fromString(Files.readString(map));
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        Trap fixed = level.createTraps(player).stream().filter(t -> t.getSpriteName().equals("quicksand"))
                .findFirst().orElseThrow();
        assertEquals(0f, PlayScreen.trapDrawY(level, fixed));
        Trap extra = TrapFactory.create('q', 15 * Level.TILE, Level.TILE, player);
        assertEquals(0f, PlayScreen.trapDrawY(level, extra));
        assertEquals(Level.TILE, extra.getY(), "procedural contact stays at foot level");
    }
}
