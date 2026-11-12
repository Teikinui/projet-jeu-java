package com.teikinui.game.view;

import com.teikinui.game.level.LevelManager;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.GameSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeathRestartTest {
    @Test
    void deathRestartsSameLevelWithoutEndingGameAndPreservesDeathCount() {
        GameSession session = new GameSession();
        LevelManager levels = new LevelManager();
        session.setCurrentLevel(levels.getCurrent().ordinal());
        session.onEvent(GameEvent.PLAYER_DIED);
        levels.onEvent(GameEvent.PLAYER_DIED);
        assertTrue(PlayScreen.shouldRestartAfterDeath(true, false));
        assertFalse(levels.isFinished());
        assertEquals(levels.getCurrent().ordinal(), session.getCurrentLevel());
        assertEquals(1, session.getDeathCount());
    }

    @Test
    void goalHasPriorityOverDeath() {
        assertFalse(PlayScreen.shouldRestartAfterDeath(true, true));
        assertFalse(PlayScreen.shouldRestartAfterDeath(false, false));
    }
}
