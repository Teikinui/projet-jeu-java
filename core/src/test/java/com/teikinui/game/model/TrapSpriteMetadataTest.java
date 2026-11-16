package com.teikinui.game.model;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.TrapType;
import com.teikinui.game.model.traps.factory.TrapFactory;

class TrapSpriteMetadataTest {
    @Test
    void factoryCarriesEachSpriteNameWithoutDependingOnLibgdx() {
        Player player = new Player(32, 32, 32, 32, 100);
        for (TrapType type : TrapType.values()) {
            Trap trap = TrapFactory.create(type.getLetter(), 64, 64, player);
            assertEquals(type.getSpriteName(), trap.getSpriteName(), type.name());
            Path sprite = Path.of("assets/sprites", type.getSpriteName() + ".png");
            assertTrue(Files.isRegularFile(sprite) || Files.isRegularFile(Path.of("..").resolve(sprite)),
                    type.name());
        }
    }
}
