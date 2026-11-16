package com.teikinui.game.level;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LevelThemeDifficultyTest {
    @Test
    void departureBoardUsesTheDifficultyShownInTheMockup() {
        assertEquals(1, LevelTheme.TAHITI.getDifficulty());
        assertEquals(2, LevelTheme.CHINE.getDifficulty());
        assertEquals(2, LevelTheme.TUNISIE.getDifficulty());
        assertEquals(3, LevelTheme.VIETNAM.getDifficulty());
        assertEquals(3, LevelTheme.SRI_LANKA.getDifficulty());
        assertEquals(3, LevelTheme.TURQUIE.getDifficulty());
        assertEquals(3, LevelTheme.FRANCE.getDifficulty());
        assertEquals(4, LevelTheme.LAPONIE.getDifficulty());
    }
}
