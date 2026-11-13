package com.teikinui.game.model.traps.falling;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public class SkyFall extends Trap {
    public SkyFall(float x, float y, Player target) {
        // Largeur de 1280 (tout l'écran), hauteur 32
        super(x, y, 1280f, 32f, target); 
        
        // S'active quand le joueur passe en dessous
        setTrigger((trap, player) -> player.getY() < trap.getY() + 100f);
    }

    @Override 
    protected boolean isTriggered() {
        return triggeredByPlayer();
    }

    @Override 
    protected void activate(float dt) {
        y -= 200f * dt; // Tombe à 200 pixels/seconde
    }

    @Override 
    public boolean isActive() {
        return true; // Tue au contact (corrigé)
    }
}