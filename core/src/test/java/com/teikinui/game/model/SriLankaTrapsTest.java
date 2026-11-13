package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.Dune;
import com.teikinui.game.model.traps.FakeChili;
import com.teikinui.game.model.traps.InvisibleBlock;
import com.teikinui.game.model.traps.Monkey;
import com.teikinui.game.model.traps.Trap;

class SriLankaTrapsTest {
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();
    private final CollisionSystem collisions = new CollisionSystem();

    SriLankaTrapsTest() {
        bus.subscribe(received::add);
    }

    private static Trap first(List<Trap> traps, Class<?> type) {
        for (Trap trap : traps) {
            if (type.isInstance(trap)) {
                return trap;
            }
        }
        throw new AssertionError("piège absent : " + type.getSimpleName());
    }

    /** Fait tourner le jeu : le joueur avance (ou pas), saute (ou pas), touche les pièges. */
    private void play(Level level, List<Trap> traps, Player player,
                      boolean right, boolean jump, float seconds) {
        for (float t = 0; t < seconds; t += 1f / 60f) {
            player.update(1f / 60f, false, right, jump);
            collisions.move(player, level, 1f / 60f);
            for (Trap trap : traps) {
                trap.update(1f / 60f);
            }
            collisions.checkContacts(player, traps, bus);
        }
    }

    @Test
    void theFakeChiliLooksLikeTheGoalButKills() {
        Level level = LevelLoader.fromString(String.join("\n",
                "########",
                "#P....v#",
                "########"));
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        assertTrue(first(traps, FakeChili.class).isVisible());

        play(level, traps, player, true, false, 3f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
        assertFalse(received.contains(GameEvent.GOAL_REACHED));
    }

    @Test
    void theInvisibleBlockAppearsWhenTheHeadHitsIt() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#####",
                "#...#",
                "#...#",
                "#.u.#",
                "#.P.#",
                "#####"));
        Player player = new Player(2 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Trap block = first(traps, InvisibleBlock.class);
        assertFalse(block.isVisible());
        assertFalse(level.isWall(2, 2));

        play(level, traps, player, false, true, 0.5f);
        assertTrue(block.isVisible());
        assertTrue(level.isWall(2, 2));
        assertTrue(player.getY() < 2 * 32f);
        assertTrue(received.isEmpty());
    }

    @Test
    void theDuneCollapsesUnderTheFeetAndTakesItsNeighbours() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#######",
                "#.....#",
                "#.www.#",
                "#.eee.#",
                "#######"));
        Player player = new Player(2 * 32f, 3 * 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        assertTrue(first(traps, Dune.class) != null);
        assertTrue(level.isWall(2, 2) && level.isWall(3, 2) && level.isWall(4, 2));

        play(level, traps, player, false, false, 1f);
        assertFalse(level.isWall(2, 2));
        assertFalse(level.isWall(3, 2));
        assertFalse(level.isWall(4, 2));
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void theMonkeyThrowsAFruitAtSomeoneStandingBelow() {
        Level level = LevelLoader.fromString(String.join("\n",
                "########",
                "#.z....#",
                "#......#",
                "#......#",
                "#.....P#",
                "########"));
        Player player = new Player(6 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Monkey monkey = (Monkey) first(traps, Monkey.class);
        float handY = monkey.getY();

        play(level, traps, player, false, false, 0.1f);
        assertTrue(monkey.getY() < handY);

        play(level, traps, player, false, false, 2f);
        assertTrue(received.contains(GameEvent.PLAYER_DIED));
    }

    @Test
    void theMonkeyIgnoresSomeoneFarAway() {
        Level level = LevelLoader.fromString(String.join("\n",
                "###########",
                "#z........#",
                "#.........#",
                "#........P#",
                "###########"));
        Player player = new Player(9 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Monkey monkey = (Monkey) first(traps, Monkey.class);
        float handY = monkey.getY();
        play(level, traps, player, false, false, 2f);
        assertTrue(monkey.getY() == handY);
        assertTrue(received.isEmpty());
    }
}