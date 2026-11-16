package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.teikinui.game.model.Player;
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
}
