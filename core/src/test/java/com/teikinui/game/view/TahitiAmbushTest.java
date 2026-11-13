package com.teikinui.game.view;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.projectile.Starfish;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TahitiAmbushTest {
    @Test void goalMovesWithoutAddingSpikesAndFixedStarfishRemains() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        Level level = LevelLoader.fromString(Files.readString(path));
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        Goal goal = level.createGoal();
        List<Trap> traps = new ArrayList<>(level.createTraps(player));
        int fixedCount = traps.size();
        long starfishCount = traps.stream().filter(Starfish.class::isInstance).count();
        float originalGoalX = goal.getX();
        player.teleport(originalGoalX - 2 * GameConfig.TILE, goal.getY());
        assertTrue(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                level.getStartX(), level.getStartY()));
        assertEquals(level.getStartX(), goal.getX());
        assertEquals(fixedCount, traps.size(), "La transition ne crée aucun nouveau piège");
        assertEquals(starfishCount, traps.stream().filter(Starfish.class::isInstance).count());
        assertFalse(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                level.getStartX(), level.getStartY()));
        assertEquals(fixedCount, traps.size());
    }
}
