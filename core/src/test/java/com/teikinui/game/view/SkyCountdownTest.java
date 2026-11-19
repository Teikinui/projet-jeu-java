package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.SkyFall;
import com.teikinui.game.model.traps.Trap;

class SkyCountdownTest {
    private static void wait(SkyFall sky, float seconds) {
        for (float t = 0; t < seconds; t += 1f / 60f) {
            sky.update(1f / 60f);
        }
    }

    @Test
    void thePanelShowsOnlyBeforeTheCurtainFalls() {
        Level level = LevelLoader.fromString("##########\n#.......a#\n#P.......#\n##########");
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        List<Trap> traps = level.createTraps(player);
        SkyFall sky = PlayScreen.findSky(traps);
        wait(sky, 1f);
        assertTrue(SkyCountdown.shows(sky));
        wait(sky, 3f);
        assertFalse(SkyCountdown.shows(sky));
        assertFalse(SkyCountdown.shows(null));
    }
}