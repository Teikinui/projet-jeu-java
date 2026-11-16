package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.teikinui.game.level.LevelTheme;

class LevelSpriteIntegrationTest {
    @Test
    void fakeChiliUsesExactlyTheGoalSpriteWhenAvailable() {
        assertEquals("goal_srilanka", PlayScreen.fakeGoalSprite(LevelTheme.SRI_LANKA, true));
        assertNull(PlayScreen.fakeGoalSprite(LevelTheme.SRI_LANKA, false));
        assertNull(PlayScreen.fakeGoalSprite(LevelTheme.TAHITI, true));
    }

    @Test
    void hiddenSlabCopiesTheRealBackgroundInsteadOfFlatSkyWhenPresent() {
        assertTrue(PlayScreen.maskHiddenSlabWithBackground(LevelTheme.LAPONIE, true));
        assertFalse(PlayScreen.maskHiddenSlabWithBackground(LevelTheme.LAPONIE, false));
        assertFalse(PlayScreen.maskHiddenSlabWithBackground(LevelTheme.TAHITI, true));
    }
}
