package com.teikinui.game.level;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Aucun tirage aléatoire de Tahiti ne doit poser un piège qui tue dès l'apparition en P. */
class TahitiSpawnSafetyTest {

    private static String tahiti() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("../assets/tahiti.txt");
        return Files.readString(path);
    }

    @Test
    void safeZoneCoversTheFiveColumnsAfterTheStart() {
        assertTrue(TahitiTrapPlacement.isInStartSafeZone(2, 1, 1, 1));
        assertTrue(TahitiTrapPlacement.isInStartSafeZone(6, 1, 1, 1));
        assertFalse(TahitiTrapPlacement.isInStartSafeZone(7, 1, 1, 1));
        assertFalse(TahitiTrapPlacement.isInStartSafeZone(3, 2, 1, 1));
    }

    @Test
    void noRandomTrapIsPlacedNextToTheStartWhateverTheSeed() throws Exception {
        String map = tahiti();
        for (long seed = 0; seed < 2000; seed++) {
            Level level = LevelLoader.fromString(map);
            Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, 150);
            List<Trap> fixed = level.createTraps(player);
            int spawnCol = (int) (level.getStartX() / Level.TILE);
            int spawnRow = (int) (level.getStartY() / Level.TILE);
            for (TahitiTrapPlacement.Placement p : TahitiTrapPlacement.plan(level, fixed, new Random(seed))) {
                assertFalse(TahitiTrapPlacement.isInStartSafeZone(p.col(), p.row(), spawnCol, spawnRow),
                        "graine " + seed + " : piège " + p + " trop près du départ");
            }
        }
    }
}
