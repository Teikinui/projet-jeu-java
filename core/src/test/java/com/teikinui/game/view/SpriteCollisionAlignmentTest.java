package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.TrapType;
import com.teikinui.game.model.traps.factory.TrapFactory;

class SpriteCollisionAlignmentTest {
    @Test
    void playerDoesNotHitCoconutBeforeTheirVisiblePixelsTouch() {
        Player player = new Player(4f, 32f, 32f, 32f, 100f);
        Trap coconut = TrapFactory.create(TrapType.COCONUT.getLetter(), 32f, 32f, player);

        assertFalse(player.collidesWith(coconut),
                "The player and coconut PNGs are still separated at this position.");

        player.teleport(7f, 32f);
        assertTrue(player.collidesWith(coconut),
                "The contact hitboxes should overlap once the visible sprites touch.");
    }
}
