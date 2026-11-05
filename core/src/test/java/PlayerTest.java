package com.teikinui.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    void testPlayerCreation() {
        Player player = new Player(100f, 100f, 32f, 32f, 200f);
        float yAvant = player.getY();
        player.update(0.1f, false, false, false);
        assertTrue(player.getY() < yAvant, "Le joueur devrait tomber");
    }

}
