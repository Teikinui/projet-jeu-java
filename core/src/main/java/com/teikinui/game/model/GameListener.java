package com.teikinui.game.model;

/** Something that reacts to game events. */
public interface GameListener {
    void onEvent(GameEvent event);
}
