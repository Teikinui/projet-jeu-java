package com.teikinui.game.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelTheme;

class ReversedInputTest {
    /** Faux clavier : on choisit à la main les touches enfoncées. */
    private static PlayerInput keys(boolean left, boolean right, boolean jump) {
        return new PlayerInput() {
            public boolean isLeft() { return left; }
            public boolean isRight() { return right; }
            public boolean isJump() { return jump; }
        };
    }

    @Test
    void leftArrowMakesThePlayerGoRight() {
        PlayerInput reversed = new ReversedInput(keys(true, false, false));
        assertTrue(reversed.isRight());
        assertFalse(reversed.isLeft());
    }

    @Test
    void rightArrowMakesThePlayerGoLeftButJumpStaysJump() {
        PlayerInput reversed = new ReversedInput(keys(false, true, true));
        assertTrue(reversed.isLeft());
        assertFalse(reversed.isRight());
        assertTrue(reversed.isJump());
    }

    @Test
    void theReversedPlayerReallyMovesTheOtherWay() {
        Player player = new Player(100f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        player.update(1f / 60f, new ReversedInput(keys(false, true, false)));
        assertTrue(player.getVx() < 0f);
    }

    @Test
    void onlyVietnamSwapsTheArrows() {
        for (LevelTheme theme : LevelTheme.values()) {
            assertTrue(theme.hasInvertedControls() == (theme == LevelTheme.VIETNAM), theme.name());
        }
    }
}