package com.teikinui.game.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckContactsTest {
    private static final class CountingThing implements Interactable {
        private int calls;

        @Override
        public void onPlayerContact(Player player, EventBus bus) {
            calls++;
        }
    }

    @Test
    void everyInteractableIsAskedOnce() {
        CountingThing first = new CountingThing();
        CountingThing second = new CountingThing();
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        new CollisionSystem().checkContacts(player, List.of(first, second), new EventBus());
        assertEquals(1, first.calls);
        assertEquals(1, second.calls);
    }
}
