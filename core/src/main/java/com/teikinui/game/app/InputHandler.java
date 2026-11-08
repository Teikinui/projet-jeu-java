package com.teikinui.game.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** Reads the arrow keys used to control the player. */
public class InputHandler {

    /** @return true while the left arrow is held down */
    public boolean isLeft() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    /** @return true while the right arrow is held down */
    public boolean isRight() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    /** @return true while the up arrow is held down */
    public boolean isJump() {
        return Gdx.input.isKeyPressed(Input.Keys.UP);
    }
}