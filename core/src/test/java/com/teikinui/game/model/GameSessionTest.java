package com.teikinui.game.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {
    @Test
    void tracksDeathsAndCurrentLevel() {
        GameSession session = new GameSession();
        assertEquals(0, session.getDeathCount());
        assertEquals(0, session.getCurrentLevel());
        session.onEvent(GameEvent.PLAYER_DIED);
        session.onEvent(GameEvent.TIME_UP);
        assertEquals(1, session.getDeathCount());
        session.setCurrentLevel(2);
        assertEquals(2, session.getCurrentLevel());
        assertThrows(IllegalArgumentException.class, () -> session.setCurrentLevel(-1));
    }
}
