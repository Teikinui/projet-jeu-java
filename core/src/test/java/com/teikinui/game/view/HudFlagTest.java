package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelTheme;

class HudFlagTest {
    @Test
    void flagSpriteNameFollowsTheCountryKey() {
        assertEquals("flag_srilanka", Hud.flagSprite(LevelTheme.SRI_LANKA));
        assertEquals("flag_france", Hud.flagSprite(LevelTheme.FRANCE));
    }

    @Test
    void everyCountryHasASmallFlagThatFitsInTheHudBar() throws Exception {
        for (LevelTheme theme : LevelTheme.values()) {
            Path path = Path.of("assets/sprites/" + Hud.flagSprite(theme) + ".png");
            if (!Files.exists(path)) path = Path.of("..", "assets", "sprites", Hud.flagSprite(theme) + ".png");
            assertTrue(Files.exists(path), path.toString());
            BufferedImage image = ImageIO.read(path.toFile());
            assertEquals(24, image.getWidth(), theme.name());
            assertEquals(16, image.getHeight(), theme.name());
        }
    }
}