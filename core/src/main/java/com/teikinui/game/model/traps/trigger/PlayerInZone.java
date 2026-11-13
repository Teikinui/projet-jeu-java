package com.teikinui.game.model.traps.trigger;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public class PlayerInZone implements Trigger {
    private float zoneX, zoneY, zoneWidth, zoneHeight;

    public PlayerInZone(float zoneX, float zoneY, float zoneWidth, float zoneHeight) {
        this.zoneX = zoneX;
        this.zoneY = zoneY;
        this.zoneWidth = zoneWidth;
        this.zoneHeight = zoneHeight;
    }

    @Override
    public boolean isTriggered(Trap trap, Player player) {
        return player.getX() + player.getWidth() > zoneX
            && player.getX() < zoneX + zoneWidth
            && player.getY() + player.getHeight() > zoneY
            && player.getY() < zoneY + zoneHeight;
    }
}