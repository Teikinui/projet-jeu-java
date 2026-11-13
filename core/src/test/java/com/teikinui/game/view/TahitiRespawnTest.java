package com.teikinui.game.view;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.level.TahitiTrapPlacement;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.factory.TrapFactory;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TahitiRespawnTest {
    @Test
    void consumedTrapResetsWhenLevelIsLoadedAgain() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        String map = Files.readString(path);
        Random random = new Random(42);
        Level first = LevelLoader.fromString(map);
        Player player = new Player(first.getStartX(), first.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> fixedTraps = first.createTraps(player);
        var plan = TahitiTrapPlacement.plan(first, fixedTraps, random);
        List<Trap> traps = TahitiTrapPlacement.instantiate(first, player, fixedTraps, plan);
        assertEquals(fixedTraps.size() + 3, traps.size());
        String firstRandomSignature = randomSignature(traps.subList(fixedTraps.size(), traps.size()));
        Trap fixed = traps.stream().filter(t -> t.getX() == 8 * Level.TILE && t.getY() == 5 * Level.TILE)
                .findFirst().orElseThrow();
        float originalY = fixed.getY();
        // Un piège consommé reste consommé pendant cette tentative.
        Trap consumed = TrapFactory.create('f', player.getX(), player.getY(), player);
        traps.add(consumed);
        consumed.onPlayerContact(player, new EventBus());
        assertEquals(Trap.State.DONE, consumed.getState());
        consumed.update(1f);
        assertEquals(Trap.State.DONE, consumed.getState());
        fixed.update(0.1f);

        int trapsBeforeGoalMoves = traps.size();
        Goal goal = first.createGoal();
        player.teleport(goal.getX() - 2 * Level.TILE, goal.getY());
        assertTrue(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal,
                first.getStartX(), first.getStartY()));
        assertEquals(trapsBeforeGoalMoves, traps.size());

        // Comme loadLevel(), recharge la carte et crée un nouveau joueur et de nouveaux pièges.
        Level next = LevelLoader.fromString(map);
        Player respawned = new Player(next.getStartX(), next.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> freshFixedTraps = next.createTraps(respawned);
        List<Trap> fresh = TahitiTrapPlacement.instantiate(next, respawned, freshFixedTraps, plan);
        assertEquals(freshFixedTraps.size() + 3, fresh.size());
        Trap fixedAgain = fresh.stream().filter(t -> t.getX() == 8 * Level.TILE && t.getY() == 5 * Level.TILE)
                .findFirst().orElseThrow();
        assertNotSame(fixed, fixedAgain);
        assertEquals(originalY, fixedAgain.getY());
        assertEquals(Trap.State.WAITING, fixedAgain.getState());
        assertNotSame(consumed, fresh.stream().filter(t -> t.getClass() == consumed.getClass()).findFirst().orElseThrow());
        assertTrue(fresh.stream().noneMatch(t -> t.getState() == Trap.State.DONE));
        assertEquals(firstRandomSignature,
                randomSignature(fresh.subList(freshFixedTraps.size(), fresh.size())));
    }

    private String randomSignature(List<Trap> traps) {
        return traps.stream().map(t -> t.getClass().getSimpleName() + ":" + t.getX() + ":" + t.getY())
                .toList().toString();
    }
}
