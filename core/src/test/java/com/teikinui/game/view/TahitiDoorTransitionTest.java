package com.teikinui.game.view;

import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.ground.ReturnDoor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TahitiDoorTransitionTest {
    @Test
    void movesDoorOnlyInTahitiAtOneTileFromGoalAndDoesNotTeleportPlayer() {
        Goal goal = new Goal(320f, 32f);
        Player player = new Player(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE - 1f,
                32f, 32f, 32f, GameConfig.RUN_SPEED);
        ReturnDoor door = new ReturnDoor(160f, 32f, player);
        door.setStart(32f, 32f);
        assertFalse(PlayScreen.moveTahitiDoorNearGoal(LevelTheme.TAHITI, player, goal, List.of(door)));
        assertEquals(160f, door.getX());

        player.teleport(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE, 32f);
        assertTrue(PlayScreen.moveTahitiDoorNearGoal(LevelTheme.TAHITI, player, goal, List.of(door)));
        assertEquals(32f, door.getX());
        assertEquals(32f, door.getY());
        assertEquals(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE, player.getX());
        assertEquals(32f, player.getY());
        assertFalse(PlayScreen.moveTahitiDoorNearGoal(LevelTheme.TAHITI, player, goal, List.of(door)));

        EventBus bus = new EventBus();
        List<GameEvent> events = new ArrayList<>();
        bus.subscribe(events::add);
        player.teleport(32f, 32f);
        door.onPlayerContact(player, bus);
        assertEquals(32f, player.getX());
        assertEquals(32f, player.getY());
        assertTrue(events.isEmpty());
    }

    @Test
    void otherCountriesDoNotMoveDoor() {
        Goal goal = new Goal(320f, 32f);
        Player player = new Player(320f - GameConfig.TILE - GameConfig.PLAYER_SIZE, 32f,
                32f, 32f, GameConfig.RUN_SPEED);
        ReturnDoor door = new ReturnDoor(160f, 32f, player);
        door.setStart(32f, 32f);
        for (LevelTheme theme : LevelTheme.values()) {
            if (theme != LevelTheme.TAHITI) {
                assertFalse(PlayScreen.moveTahitiDoorNearGoal(theme, player, goal, List.of(door)));
                assertEquals(160f, door.getX());
            }
        }
    }
}
