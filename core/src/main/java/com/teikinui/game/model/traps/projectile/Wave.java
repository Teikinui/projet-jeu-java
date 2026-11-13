package com.teikinui.game.model.traps.projectile;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public class Wave extends Trap {
    private float speed = 300f;

    public Wave(float x, float y, Player target) {
        super(x, y, 64f, 64f, target);
        
        // S'active quand le joueur est sur la même ligne (proche)
        setTrigger((trap, player) -> Math.abs(player.getY() - trap.getY()) < 100f);
    }

    @Override 
    protected boolean isTriggered() {
        return triggeredByPlayer();
    }

    @Override 
    protected void activate(float dt) {
        x += speed * dt; // Fonce vers la droite
    }

    @Override 
    public boolean isActive() {
        return true; // Tue au contact (corrigé)
    }
}