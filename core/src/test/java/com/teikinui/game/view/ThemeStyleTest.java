package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.Color;
import com.teikinui.game.level.LevelTheme;

class ThemeStyleTest {
    @ParameterizedTest
    @EnumSource(LevelTheme.class)
    void everyCountryHasColorsAndATaunt(LevelTheme theme) {
        ThemeStyle style = ThemeStyle.of(theme);

        assertNotNull(style.hudBar());
        assertNotNull(style.accent());
        assertNotNull(style.soft());
        assertNotNull(style.halo());
        assertNotNull(style.sky());
        assertFalse(style.taunt().isBlank());
    }

    @Test
    void tahitiUsesThePaletteFromTheMockup() {
        ThemeStyle style = ThemeStyle.of(LevelTheme.TAHITI);

        assertEquals(Color.valueOf("#0E3B3C"), style.hudBar());
        assertEquals(Color.valueOf("#FF8A7E"), style.accent());
        assertEquals(Color.valueOf("#FFD166"), style.halo());
    }
}
