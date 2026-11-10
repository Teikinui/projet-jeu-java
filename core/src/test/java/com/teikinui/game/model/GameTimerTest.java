package com.teikinui.game.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTimerTest {
    @Test
    void countsDownToZeroWithoutGoingNegative() {
        GameTimer timer = new GameTimer();
        assertEquals(480f, timer.getRemaining());
        assertFalse(timer.isTimeUp());
        timer.update(1.5f);
        assertEquals(478.5f, timer.getRemaining());
        timer.update(500f);
        assertEquals(0f, timer.getRemaining());
        assertTrue(timer.isTimeUp());
    }

    @Test
    void rejectsNegativeDelta() {
        GameTimer timer = new GameTimer();
        assertThrows(IllegalArgumentException.class, () -> timer.update(-1f));
    }
}
