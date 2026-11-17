package com.teikinui.game.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

import com.teikinui.game.model.ContactBounds;

class LevelThemeGoalBoundsTest {
    @Test
    void goalContactBoundsMatchTheirPngAlphaBounds() throws Exception {
        for (LevelTheme theme : LevelTheme.values()) {
            String themeKey = theme.name().toLowerCase(Locale.ROOT).replace("_", "");
            Path path = Path.of("assets/sprites/goal_" + themeKey + ".png");
            if (!Files.exists(path)) path = Path.of("..", "assets", "sprites", "goal_" + themeKey + ".png");
            BufferedImage image = ImageIO.read(path.toFile());
            assertNotNull(image, path.toString());

            int left = image.getWidth();
            int top = image.getHeight();
            int right = -1;
            int bottom = -1;
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (((image.getRGB(x, y) >>> 24) & 255) == 0) continue;
                    left = Math.min(left, x);
                    top = Math.min(top, y);
                    right = Math.max(right, x + 1);
                    bottom = Math.max(bottom, y + 1);
                }
            }

            ContactBounds expected = ContactBounds.fromAlphaBounds(
                    image.getWidth(), image.getHeight(), left, top, right, bottom);
            assertEquals(expected, theme.getGoalContactBounds(), theme.name());
        }
    }
}
