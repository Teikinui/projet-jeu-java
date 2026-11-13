package com.teikinui.game.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class GameTimerTest {
    @Test
    void countsDownToZeroWithoutGoingNegative() {
        GameTimer timer = new GameTimer();
        assertEquals(180f, timer.getRemaining());
        assertFalse(timer.isTimeUp());
        timer.update(1.5f);
        assertEquals(178.5f, timer.getRemaining());
        timer.update(500f);
        assertEquals(0f, timer.getRemaining());
        assertTrue(timer.isTimeUp());
    }

    @Test
    void resetPutsTheClockBackToThreeMinutes() {
        GameTimer timer = new GameTimer();
        timer.update(100f);
        timer.reset();
        assertEquals(180f, timer.getRemaining());
    }

    @Test
    void rejectsNegativeDelta() {
        GameTimer timer = new GameTimer();
        assertThrows(IllegalArgumentException.class, () -> timer.update(-1f));
    }
}