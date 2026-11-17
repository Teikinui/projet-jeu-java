package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.teikinui.game.level.LevelTheme;

class MessageScreenBoardTest {
    @Test
    void timeoutTicketShowsCurrentCountryAndCompletedCountries() {
        var ticket = MessageScreen.Board.forResult(LevelTheme.CHINE, 5, 0f, false);
        assertEquals("TT 02", ticket.flight());
        assertEquals("CHINE", ticket.destination());
        assertEquals("1 SUR 8", ticket.completed());
        assertEquals("00:00", ticket.clock());
        assertEquals("B2", ticket.gate());
        assertEquals("T'es mort 5 fois.", ticket.deathsLabel());
        assertFalse(ticket.victory());
    }

    @Test
    void endScreenKeysRestartOrReturnToMenuOnlyOnce() {
        assertEquals(MessageScreen.Action.RESTART, MessageScreen.actionFor(false, true, false, false));
        assertEquals(MessageScreen.Action.MENU, MessageScreen.actionFor(false, false, true, false));
        assertEquals(MessageScreen.Action.MENU, MessageScreen.actionFor(false, false, false, true));
        assertEquals(MessageScreen.Action.RESTART, MessageScreen.actionFor(false, true, true, true));
        assertEquals(MessageScreen.Action.NONE, MessageScreen.actionFor(false, false, false, false));
        assertEquals(MessageScreen.Action.NONE, MessageScreen.actionFor(true, true, true, true));
    }

    @Test
    void victoryTicketCompletesAllCountriesAndKeepsRemainingTime() {
        var ticket = MessageScreen.Board.forResult(LevelTheme.LAPONIE, 2, 61f, true);
        assertEquals("8 SUR 8", ticket.completed());
        assertEquals("01:01", ticket.clock());
        assertEquals("H8", ticket.gate());
        assertTrue(ticket.victory());
        assertThrows(IllegalArgumentException.class,
                () -> MessageScreen.Board.forResult(null, 0, 0f, false));
    }
}
