package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MenuScreenLayoutTest {
    @Test
    void taglineLeavesTheRequiredGapAboveTheActionButton() {
        float taglineBottom = MenuScreen.taglineTopFromTop() + MenuScreen.TAGLINE_BLOCK_HEIGHT;
        float latestAllowedBottom = MenuScreen.MENU_BUTTON_TOP_FROM_TOP - MenuScreen.TAGLINE_BUTTON_GAP;

        assertTrue(taglineBottom <= latestAllowedBottom);
        assertEquals(214f, MenuScreen.taglineTopFromTop());
    }
}
