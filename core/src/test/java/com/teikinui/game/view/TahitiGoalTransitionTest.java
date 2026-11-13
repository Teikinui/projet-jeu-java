package com.teikinui.game.view;

import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TahitiGoalTransitionTest {
    @Test
    void goalMovesToSpawnOnceAtOneTileAndPlayerStaysPut() {
        Goal goal = new Goal(320f, 32f);
        Player player = new Player(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE - 1f,
                32f, 32f, 32f, GameConfig.RUN_SPEED);
        assertFalse(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal, 32f, 32f));
        assertEquals(320f, goal.getX());

        player.teleport(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE, 32f);
        assertTrue(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal, 32f, 32f));
        assertEquals(32f, goal.getX());
        assertEquals(32f, goal.getY());
        assertEquals(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE, player.getX());
        assertEquals(32f, player.getY());
        assertFalse(PlayScreen.moveTahitiGoalNearPlayer(LevelTheme.TAHITI, player, goal, 32f, 32f));

        EventBus bus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        bus.subscribe(events::add);
        player.teleport(32f, 32f);
        goal.onPlayerContact(player, bus);
        assertEquals(List.of(GameEvent.GOAL_REACHED), events);
    }

    @Test
    void goalRemainsInOtherCountries() {
        Goal goal = new Goal(320f, 32f);
        Player player = new Player(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE, 32f,
                32f, 32f, GameConfig.RUN_SPEED);
        for (LevelTheme theme : LevelTheme.values()) {
            if (theme != LevelTheme.TAHITI) {
                assertFalse(PlayScreen.moveTahitiGoalNearPlayer(theme, player, goal, 32f, 32f));
                assertEquals(320f, goal.getX());
            }
        }
    }
}
