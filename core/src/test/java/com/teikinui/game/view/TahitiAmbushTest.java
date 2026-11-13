package com.teikinui.game.view;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.GoalSpike;
import com.teikinui.game.model.traps.Trap;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TahitiAmbushTest {
    @Test void spikesBetweenUpperStarfishAndOriginalGoalOnlyAfterGoalMoves() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        Level level = LevelLoader.fromString(Files.readString(path));
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        Goal goal = level.createGoal();
        List<Trap> traps = new ArrayList<>(level.createTraps(player));
        assertEquals(0, traps.stream().filter(GoalSpike.class::isInstance).count());
        float originalGoalX = goal.getX();
        player.teleport(originalGoalX - 2 * GameConfig.TILE, goal.getY());
        assertTrue(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                level.getStartX(), level.getStartY(), level, traps));
        List<GoalSpike> spikes = traps.stream().filter(GoalSpike.class::isInstance).map(GoalSpike.class::cast).toList();
        assertEquals(3, spikes.size());
        for (int col = 25; col <= 27; col++) {
            int cell = col;
            assertTrue(spikes.stream().anyMatch(spike -> spike.getX() == cell * GameConfig.TILE));
        }
        spikes.forEach(spike -> { assertTrue(spike.isVisible()); assertTrue(spike.isActive()); });
        assertFalse(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                level.getStartX(), level.getStartY(), level, traps));
        assertEquals(3, traps.stream().filter(GoalSpike.class::isInstance).count());
    }
}
