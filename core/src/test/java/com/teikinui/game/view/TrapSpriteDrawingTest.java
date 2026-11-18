package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.Hitbox;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.TrapType;
import com.teikinui.game.model.traps.factory.TrapFactory;

class TrapSpriteDrawingTest {
    private final Player player = new Player(32, 32, 32, 32, 100);

    private Trap create(TrapType type) {
        return TrapFactory.create(type.getLetter(), 64, 64, player);
    }

    @Test
    void supportsAllAvailableTrapSpritesAndPureShapeExceptions() {
        for (TrapType type : TrapType.values()) {
            Trap trap = create(type);
            boolean special = type == TrapType.FAKE_CHILI || type == TrapType.HIDDEN_SLAB
                    || type == TrapType.SKY_FALL || type == TrapType.TILT_SLAB
                    || type == TrapType.INVISIBLE_BLOCK;
            assertEquals(!special, PlayScreen.usesTrapSprite(trap, true), type.name());
            assertFalse(PlayScreen.usesTrapSprite(trap, false), type.name());
        }
    }

    @Test
    void spritesKeepBackdropsButDoNotDrawFallbackOverThem() {
        for (TrapType type : TrapType.values()) {
            Trap trap = create(type);
            boolean backdrop = type == TrapType.MONKEY || type == TrapType.SIMIT_VENDOR
                    || type == TrapType.CROISSANT;
            if (PlayScreen.usesTrapSprite(trap, true)) {
                assertEquals(backdrop, PlayScreen.drawsTrapShape(trap, true), type.name());
            } else {
                assertTrue(PlayScreen.drawsTrapShape(trap, true), type.name());
            }
            assertTrue(PlayScreen.drawsTrapShape(trap, false), type.name());
        }
    }

    @Test
    void floorImitatorsCopyTheOpaqueFloorAndKeepFullContactBounds() throws Exception {
        for (TrapType type : new TrapType[] {TrapType.FAKE_SLAB, TrapType.CRUMBLE_BLOCK,
                TrapType.FAKE_FLOOR}) {
            Trap trap = create(type);
            assertTrue(PlayScreen.copiesFloor(trap));
            assertEquals(trap.getWidth(), trap.getHitbox().getWidth(), 0.001f);
            assertEquals(trap.getHeight(), trap.getHitbox().getHeight(), 0.001f);
        }
        for (String country : new String[] {"tahiti", "chine", "tunisie", "vietnam",
                "srilanka", "turquie", "france", "laponie"}) {
            Path path = Path.of("assets/sprites/floor_" + country + ".png");
            if (!Files.exists(path)) path = Path.of("..").resolve(path);
            BufferedImage image = ImageIO.read(path.toFile());
            assertEquals(32, image.getWidth());
            assertEquals(32, image.getHeight());
            for (int y = 0; y < 32; y++) for (int x = 0; x < 32; x++)
                assertEquals(255, image.getRGB(x, y) >>> 24, country + " (" + x + "," + y + ")");
        }
    }

    @Test
    void trapContactBoundsMatchTheOpaquePixelsOfTheirPngs() throws Exception {
        for (TrapType type : TrapType.values()) {
            Path path = Path.of("assets/sprites", type.getSpriteName() + ".png");
            if (!Files.exists(path)) path = Path.of("..", "assets", "sprites", type.getSpriteName() + ".png");
            if (!Files.exists(path)) continue;

            BufferedImage image = ImageIO.read(path.toFile());
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

            Trap trap = create(type);
            Hitbox hitbox = trap.getHitbox();
            if (right < 0) {
                assertEquals(trap.getX(), hitbox.getX(), type.name());
                assertEquals(trap.getY(), hitbox.getY(), type.name());
                assertEquals(trap.getWidth(), hitbox.getWidth(), type.name());
                assertEquals(trap.getHeight(), hitbox.getHeight(), type.name());
                continue;
            }

            float expectedX = trap.getX() + trap.getWidth() * left / image.getWidth();
            float expectedY = trap.getY() + trap.getHeight() * (image.getHeight() - bottom) / image.getHeight();
            float expectedWidth = trap.getWidth() * (right - left) / image.getWidth();
            float expectedHeight = trap.getHeight() * (bottom - top) / image.getHeight();
            assertEquals(expectedX, hitbox.getX(), 0.001f, type.name());
            assertEquals(expectedY, hitbox.getY(), 0.001f, type.name());
            assertEquals(expectedWidth, hitbox.getWidth(), 0.001f, type.name());
            assertEquals(expectedHeight, hitbox.getHeight(), 0.001f, type.name());
        }
    }
}
