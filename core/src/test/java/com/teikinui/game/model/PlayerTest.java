package com.teikinui.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    void playerUpdateCalculatesDownwardGravity() {
        Player player = new Player(100f, 100f, 32f, 32f, 200f);
        player.update(0.1f, false, false, false);
        assertTrue(player.getVy() < 0f, "La vitesse verticale devrait être négative en chute");
    }

}
