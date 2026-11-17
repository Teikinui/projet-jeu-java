package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TauntBubbleTest {
    @Test
    void visibilityTimerClampsAtZeroAndRejectsInvalidTimeSteps() {
        assertEquals(1.5f, TauntBubble.remainingAfter(2f, 0.5f));
        assertEquals(0f, TauntBubble.remainingAfter(0.25f, 1f));
        assertThrows(IllegalArgumentException.class, () -> TauntBubble.remainingAfter(1f, -0.1f));
    }
}
