package com.teikinui.game.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.model.GameEvent;

class LevelManagerTest {
    @Test
    void theTripGoesTahitiThenChinaThenTunisiaThenVietnam() {
        LevelManager levels = new LevelManager();
        assertEquals(LevelTheme.TAHITI, levels.getCurrent());
        levels.onEvent(GameEvent.GOAL_REACHED);
        assertEquals(LevelTheme.CHINE, levels.getCurrent());
        levels.onEvent(GameEvent.GOAL_REACHED);
        assertEquals(LevelTheme.TUNISIE, levels.getCurrent());
        levels.onEvent(GameEvent.GOAL_REACHED);
        assertEquals(LevelTheme.VIETNAM, levels.getCurrent());
        assertFalse(levels.isFinished());
        levels.onEvent(GameEvent.GOAL_REACHED);
        assertTrue(levels.isFinished());
        assertEquals(LevelTheme.VIETNAM, levels.getCurrent());
    }

    @Test
    void dyingDoesNotChangeTheCountry() {
        LevelManager levels = new LevelManager();
        levels.onEvent(GameEvent.PLAYER_DIED);
        levels.onEvent(GameEvent.TIME_UP);
        assertEquals(LevelTheme.TAHITI, levels.getCurrent());
        assertFalse(levels.isFinished());
    }
}