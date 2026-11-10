package com.teikinui.game.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.teikinui.game.model.PlayerInput;

/** Lit les touches utilisées pour contrôler le joueur. */
public final class InputHandler implements PlayerInput {
    public boolean isLeft() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    public boolean isRight() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    public boolean isJump() {
        return Gdx.input.isKeyPressed(Input.Keys.UP);
    }
}
