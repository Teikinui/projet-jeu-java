package com.teikinui.game.model.traps.ground;

import com.teikinui.game.model.traps.Trap;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/** Sable mouvant : ralentit pendant le contact, sans tuer. */
public final class QuickSand extends Trap {
    public QuickSand(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
    }

    private boolean touchesSand(Player player) {
        // Dans la carte Tahiti q est une tuile solide : les pieds reposent sur son bord supérieur.
        boolean standingOnTop = Math.abs(player.getY() - (y + height)) < 1f
                && player.getHitbox().getX() < x + width
                && player.getHitbox().getX() + player.getHitbox().getWidth() > x;
        return collidesWith(player) || standingOnTop;
    }

    @Override protected boolean isTriggered() { return touchesSand(target); }
    @Override protected void activate(float dt) { }

    @Override public void onPlayerContact(Player player, EventBus bus) {
        if (touchesSand(player)) player.slowNextFrame();
        else state = State.WAITING;
    }

    @Override public boolean isActive() { return false; }

    @Override public boolean isMarked() { return true; }
}
