package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import com.teikinui.game.model.Player;

class PlayerDrawBoundsTest {
    @Test
    void spriteIsCenteredAndMirroredAroundTheCollisionBox() {
        Player player = new Player(96, 64, 32, 32, 100);
        var bounds = PlayScreen.playerDrawBounds(player);
        assertEquals(80f, bounds.x());
        assertEquals(52f, bounds.y());
        assertEquals(64f, bounds.size());
        assertEquals(112f, bounds.x() + bounds.size() / 2f);
        assertEquals(144f, bounds.x() + bounds.size());
    }

    @Test
    void existingFramesAreSquareAndOpaqueFeetStayNearHitboxFloor() throws Exception {
        Player player = new Player(96, 64, 32, 32, 100);
        var bounds = PlayScreen.playerDrawBounds(player);
        for (String action : new String[] {"idle", "walk", "jump"}) {
            int count = 0;
            for (int i = 0; ; i++) {
                Path path = Path.of("assets/sprites/player_" + action + "_" + i + ".png");
                if (!Files.exists(path)) path = Path.of("..").resolve(path);
                if (!Files.exists(path)) break;
                BufferedImage image = ImageIO.read(path.toFile());
                assertEquals(image.getWidth(), image.getHeight(), path.toString());
                int bottom = -1;
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        if (((image.getRGB(x, y) >>> 24) & 255) > 127) bottom = Math.max(bottom, y);
                    }
                }
                assertTrue(bottom >= 0, path.toString());
                float feetY = bounds.y() + (image.getHeight() - 1 - bottom) * bounds.size() / image.getHeight();
                assertTrue(Math.abs(feetY - player.getY()) <= 12f, path + " foot=" + feetY);
                count++;
            }
            assertTrue(count > 0, "Missing " + action + " frames");
        }
    }
}
