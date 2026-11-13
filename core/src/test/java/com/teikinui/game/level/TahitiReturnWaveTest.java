package com.teikinui.game.level;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.falling.CocoFall;
import com.teikinui.game.model.traps.projectile.Starfish;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TahitiReturnWaveTest {
    private String map() throws Exception {
        Path path = Path.of("assets/tahiti.txt");
        if (!Files.exists(path)) path = Path.of("..", "assets", "tahiti.txt");
        return Files.readString(path);
    }

    @Test void fixedPlanRecreatesSameTypesPositionsAndFreshStatesAfterDeath() throws Exception {
        String text = map();
        Level level = LevelLoader.fromString(text);
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        var plan = TahitiTrapPlacement.plan(level, level.createTraps(player), new Random(42));
        List<Trap> first = TahitiTrapPlacement.instantiate(level, player, level.createTraps(player), plan);
        String positions = signature(first);
        first.get(first.size() - 1).update(0.2f);
        Level retry = LevelLoader.fromString(text);
        Player respawned = new Player(retry.getStartX(), retry.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> second = TahitiTrapPlacement.instantiate(retry, respawned, retry.createTraps(respawned), plan);
        assertEquals(positions, signature(second));
        assertNotSame(first.get(first.size()-1), second.get(second.size()-1));
        assertEquals(Trap.State.WAITING, second.get(second.size()-1).getState());
    }

    @Test void waveSpawnsOnlyOnceAfterGoalMovesAndRearmsOnNextAttempt() throws Exception {
        Level level = LevelLoader.fromString(map());
        Player player = new Player(level.getStartX(), level.getStartY(), 32, 32, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        int fixed = traps.size();
        assertEquals(fixed, traps.size());
        assertEquals(3, TahitiTrapPlacement.returnWave(level, player).size());
        assertEquals(2, TahitiTrapPlacement.returnWave(level, player).stream().filter(CocoFall.class::isInstance).count());
        assertEquals(1, TahitiTrapPlacement.returnWave(level, player).stream().filter(Starfish.class::isInstance).count());
        for (Trap trap : TahitiTrapPlacement.returnWave(level, player)) {
            int col = (int)(trap.getX()/Level.TILE), row = (int)(trap.getY()/Level.TILE);
            assertEquals(0, level.getTrapLetter(col,row));
            assertFalse(level.isWall(col,row));
            assertFalse(trap.collidesWith(player));
        }
        Level retry = LevelLoader.fromString(map());
        List<Trap> nextWave = TahitiTrapPlacement.returnWave(retry, player);
        assertEquals(signature(TahitiTrapPlacement.returnWave(level, player)), signature(nextWave));
        assertNotSame(nextWave.get(0), TahitiTrapPlacement.returnWave(level, player).get(0));
    }

    private String signature(List<Trap> traps) {
        return traps.stream().map(t -> t.getClass().getSimpleName() + ":" + t.getX() + ":" + t.getY()).toList().toString();
    }
}
