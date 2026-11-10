package com.teikinui.game.model;

/** Something that reacts when the player touches it. */
public interface Interactable {
    void onPlayerContact(Player player, EventBus bus);
}
