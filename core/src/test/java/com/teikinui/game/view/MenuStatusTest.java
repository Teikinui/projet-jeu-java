package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MenuStatusTest {
    @Test
    void statusAndColorFollowTheBombCount() {
        assertEquals("À L'HEURE", MenuScreen.statusText(1));
        assertEquals("RETARDÉ", MenuScreen.statusText(2));
        assertEquals("ANNULÉ ?", MenuScreen.statusText(3));
        assertEquals("PERDU", MenuScreen.statusText(4));

        assertEquals(Palette.ON_TIME, MenuScreen.statusColor(1));
        assertEquals(Palette.LATE, MenuScreen.statusColor(2));
        assertEquals(Palette.CANCELLED, MenuScreen.statusColor(3));
        assertEquals(Palette.LOST, MenuScreen.statusColor(4));
    }
}
