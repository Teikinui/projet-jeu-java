package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerSpritesTest {
    @Test
    void countryAnimationTakesPrecedenceOverTheBaseAnimation() {
        Animation<TextureRegion> country = animation();
        Animation<TextureRegion> base = animation();

        assertSame(country, PlayerSprites.preferCountryAnimation(country, base));
    }

    @Test
    void baseAnimationIsUsedWhenCountryAnimationIsMissing() {
        Animation<TextureRegion> base = animation();

        assertSame(base, PlayerSprites.preferCountryAnimation(null, base));
        assertNull(PlayerSprites.preferCountryAnimation(null, null));
    }

    @Test
    void animationStateFollowsDeathAirMovementAndIdlePriority() {
        assertEquals(PlayerSprites.State.DIE, PlayerSprites.stateFor(150f, false, true));
        assertEquals(PlayerSprites.State.JUMP, PlayerSprites.stateFor(0f, false, false));
        assertEquals(PlayerSprites.State.WALK, PlayerSprites.stateFor(-150f, true, false));
        assertEquals(PlayerSprites.State.IDLE, PlayerSprites.stateFor(0f, true, false));
    }

    private static Animation<TextureRegion> animation() {
        return new Animation<>(0.1f, new TextureRegion());
    }
}
