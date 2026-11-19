package com.teikinui.game.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class LevelThemeDifficultyTest {
    @Test
    void departureBoardUsesTheDifficultyShownInTheMockup() {
        assertEquals(2, LevelTheme.TAHITI.getDifficulty());
        assertEquals(3, LevelTheme.CHINE.getDifficulty());
        assertEquals(3, LevelTheme.TUNISIE.getDifficulty());
        assertEquals(2, LevelTheme.VIETNAM.getDifficulty());
        assertEquals(2, LevelTheme.SRI_LANKA.getDifficulty());
        assertEquals(1, LevelTheme.TURQUIE.getDifficulty());
        assertEquals(1, LevelTheme.FRANCE.getDifficulty());
        assertEquals(3, LevelTheme.LAPONIE.getDifficulty());
    }
}
