package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.traps.Croissant;
import com.teikinui.game.model.traps.Trap;

class CroissantTest {
    private static final float DT = 1f / 60f;

    @Test
    void aBoostedPlayerRunsFasterAndJumpsHigher() {
        Player normal = new Player(0f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        Player boosted = new Player(0f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        boosted.boost(Croissant.BOOST_TIME);

        normal.update(DT, false, true, true);
        boosted.update(DT, false, true, true);

        assertTrue(boosted.isBoosted());
        assertTrue(boosted.getVx() > normal.getVx());
        assertTrue(boosted.getVy() > normal.getVy());
    }

    @Test
    void theBoostStopsAfterAFewSeconds() {
        Player player = new Player(0f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        player.boost(Croissant.BOOST_TIME);
        for (float t = 0; t < Croissant.BOOST_TIME + 0.1f; t += DT) {
            player.update(DT, false, false, false);
        }
        assertFalse(player.isBoosted());
        player.update(DT, false, true, false);
        assertEquals(GameConfig.RUN_SPEED, player.getVx(), 0.01f);
    }

    @Test
    void jumpingIntoTheCroissantEatsItAndGivesTheBoost() {
        Level level = LevelLoader.fromString(String.join("\n",
                "#####",
                "#...#",
                "#.g.#",
                "#...#",
                "#.P.#",
                "#####"));
        Player player = new Player(2 * 32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        Croissant croissant = (Croissant) traps.get(0);
        EventBus bus = new EventBus();
        List<GameEvent> received = new ArrayList<>();
        bus.subscribe(received::add);
        CollisionSystem collisions = new CollisionSystem();
        assertFalse(croissant.isEaten());

        for (float t = 0; t < 0.6f; t += DT) {
            player.update(DT, false, false, t < 0.2f);
            collisions.move(player, level, DT);
            croissant.update(DT);
            collisions.checkContacts(player, traps, bus);
        }

        assertTrue(croissant.isEaten());
        assertTrue(player.isBoosted());
        assertTrue(received.isEmpty());
    }
}