package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelTheme;

class LevelSpriteIntegrationTest {
    @Test
    void fakeChiliUsesExactlyTheGoalSpriteWhenAvailable() {
        assertEquals("goal_srilanka", PlayScreen.fakeGoalSprite(LevelTheme.SRI_LANKA, true));
        assertNull(PlayScreen.fakeGoalSprite(LevelTheme.SRI_LANKA, false));
        assertEquals("goal_tahiti", PlayScreen.fakeGoalSprite(LevelTheme.TAHITI, true));
        assertEquals("goal_chine", PlayScreen.fakeGoalSprite(LevelTheme.CHINE, true),
                "En Chine, le faux objectif est le bol de nouilles (la vraie sortie reste en pics)");
        assertEquals("goal_turquie", PlayScreen.fakeGoalSprite(LevelTheme.TURQUIE, true));
    }

    @Test
    void fakeBowlInChinaTouchesLikeTheBowlDrawing() {
        assertEquals(LevelTheme.CHINE.getGoalContactBounds(),
                PlayScreen.fakeGoalContactBounds(LevelTheme.CHINE, true));
        assertEquals(PlayScreen.goalContactBounds(LevelTheme.TURQUIE, true),
                PlayScreen.fakeGoalContactBounds(LevelTheme.TURQUIE, true));
    }

    @Test
    void hiddenSlabCopiesTheRealBackgroundInsteadOfFlatSkyWhenPresent() {
        assertTrue(PlayScreen.maskHiddenSlabWithBackground(LevelTheme.LAPONIE, true));
        assertFalse(PlayScreen.maskHiddenSlabWithBackground(LevelTheme.LAPONIE, false));
        assertFalse(PlayScreen.maskHiddenSlabWithBackground(LevelTheme.TAHITI, true));
    }
}