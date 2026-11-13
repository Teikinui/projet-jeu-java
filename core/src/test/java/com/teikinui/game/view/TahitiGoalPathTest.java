package com.teikinui.game.view;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.CollisionSystem;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameEvent;
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

class TahitiGoalPathTest {
    @Test
    void walkingAlongUpperPlatformMakesGoalMoveBeforeFirstDeath() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        Level level = LevelLoader.fromString(Files.readString(path));
        Goal goal = level.createGoal();
        // Rangée du G, juste avant le rayon de déclenchement de l'étoile à la colonne 24.
        Player player = new Player(19 * Level.TILE, goal.getY(), GameConfig.PLAYER_SIZE,
                GameConfig.PLAYER_SIZE, GameConfig.RUN_SPEED);
        List<Trap> traps = new ArrayList<>(level.createTraps(player));
        EventBus bus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        bus.subscribe(events::add);
        CollisionSystem collisions = new CollisionSystem();
        boolean moved = false;
        for (int frame = 0; frame < 80 && events.isEmpty() && !moved; frame++) {
            float step = 1f / 30f;
            player.update(step, false, true, false);
            collisions.move(player, level, step);
            moved = PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                    level.getStartX(), level.getStartY());
            PlayScreen.updateTraps(LevelTheme.TAHITI, level, goal, traps, step);
            collisions.checkContacts(player, traps, bus);
            collisions.checkFall(player, bus);
            collisions.checkContacts(player, List.of(goal), bus);
        }
        assertTrue(moved, "G doit rejoindre P avant le premier événement : " + events
                + ", joueur x=" + player.getX() + ", G x=" + goal.getX());
        assertEquals(level.getStartX(), goal.getX());
    }

    @Test
    void guardingStarfishWaitsUntilGoalMoves() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        Level level = LevelLoader.fromString(Files.readString(path));
        Goal goal = level.createGoal();
        Player player = new Player(21 * Level.TILE, goal.getY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> traps = new ArrayList<>(level.createTraps(player));
        Starfish guard = traps.stream().filter(Starfish.class::isInstance).map(Starfish.class::cast)
                .filter(t -> t.getY() == goal.getY()).findFirst().orElseThrow();
        PlayScreen.updateTraps(LevelTheme.TAHITI, level, goal, traps, 1f / 30f);
        assertEquals(Trap.State.WAITING, guard.getState());
        player.teleport(goal.getX() - 2 * Level.TILE, goal.getY());
        assertTrue(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                level.getStartX(), level.getStartY()));
        PlayScreen.updateTraps(LevelTheme.TAHITI, level, goal, traps, 1f / 30f);
        assertEquals(Trap.State.TRIGGERED, guard.getState());
    }
}
