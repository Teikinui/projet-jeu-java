package com.teikinui.game.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoalTest {
    private final EventBus bus = new EventBus();
    private final List<GameEvent> received = new ArrayList<>();

    GoalTest() {
        bus.subscribe(received::add);
    }

    @Test
    void touchingTheGoalAnnouncesItOnlyOnce() {
        Goal goal = new Goal(64f, 32f);
        Player player = new Player(64f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        goal.onPlayerContact(player, bus);
        goal.onPlayerContact(player, bus);
        assertEquals(List.of(GameEvent.GOAL_REACHED), received);
        assertTrue(goal.isReached());
    }

    @Test
    void aFarAwayPlayerDoesNotReachTheGoal() {
        Goal goal = new Goal(640f, 32f);
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        goal.onPlayerContact(player, bus);
        assertTrue(received.isEmpty());
        assertFalse(goal.isReached());
    }
}