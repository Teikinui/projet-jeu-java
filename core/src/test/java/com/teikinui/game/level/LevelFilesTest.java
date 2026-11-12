package com.teikinui.game.level;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Chaque pays doit remplir exactement l'écran : 30 x 17 cases, un départ, un objectif. */
class LevelFilesTest {
    private static Path assetFile(String name) {
        Path fromRoot = Path.of("assets", name);
        return Files.exists(fromRoot) ? fromRoot : Path.of("..", "assets", name);
    }

    @Test
    void everyCountryFillsTheWholeScreen() throws IOException {
        for (LevelTheme theme : LevelTheme.values()) {
            Path file = assetFile(theme.getFileName());
            assertTrue(Files.exists(file), "Fichier manquant : " + theme.getFileName());
            List<String> lines = Files.readString(file).strip().lines().toList();
            assertEquals(GameConfig.LEVEL_ROWS, lines.size(), theme + " : nombre de lignes");
            for (String line : lines) {
                assertEquals(GameConfig.LEVEL_COLS, line.length(), theme + " : largeur de ligne");
            }
            String all = String.join("", lines);
            assertEquals(1, all.chars().filter(c -> c == 'P').count(), theme + " : un seul départ P");
            assertEquals(1, all.chars().filter(c -> c == 'G').count(), theme + " : un seul objectif G");
        }
    }

    @Test
    void everyCountryLoadsWithItsTrapsAndGoal() throws IOException {
        for (LevelTheme theme : LevelTheme.values()) {
            Level level = LevelLoader.fromString(Files.readString(assetFile(theme.getFileName())));
            Player player = new Player(level.getStartX(), level.getStartY(), 32f, 32f, GameConfig.RUN_SPEED);
            assertNotNull(level.createTraps(player));
            assertNotNull(level.createGoal());
        }
    }
}