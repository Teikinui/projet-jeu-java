package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.Player;

/**
 * Ciel qui tombe (Chine) : un décompte 3, 2, 1 prévient le joueur, puis le ciel descend sur toute
 * la largeur de l'écran, jusqu'à toucher la tête du joueur. Tant qu'il est en bas, c'est « 1, 2, 3, soleil » :
 * si le joueur bouge (court, saute ou tombe), il meurt. Ensuite le ciel remonte.
 */
public final class SkyFall extends Trap {
    private static final float WAIT = 0.5f;
    /** Décompte avant la chute : le joueur a le temps de s'arrêter. */
    private static final float WARN = 3f;
    private static final float DOWN = 1f;
    private static final float HOLD = 1.5f;
    private static final float UP = 0.8f;
    private static final float FALL = WAIT + WARN;
    private static final float TOP = GameConfig.LEVEL_ROWS * GameConfig.TILE;
    /** Petit espace entre le ciel et la tête du joueur. */
    private static final float GAP = 2f;

    private float time;
    private float lowest = TOP;
    private boolean aimed;

    public SkyFall(float x, float y, Player target) {
        super(0f, 0f, 0f, 0f, target);
        setMarked(false);
    }

    @Override
    protected boolean isTriggered() {
        return true;
    }

    @Override
    protected void activate(float dt) {
        if (!aimed && time >= FALL) {
            // Au moment où il commence à tomber, le ciel vise la tête du joueur.
            lowest = Math.min(TOP, target.getY() + target.getHeight() + GAP);
            aimed = true;
        }
        time += dt;
        if (time > FALL + DOWN + HOLD + UP) {
            state = State.DONE;
        }
    }

    /** Vrai pendant le décompte qui annonce la chute : bouger ne tue pas encore. */
    public boolean isWarning() {
        return time >= WAIT && time < FALL;
    }

    /** Vrai pendant que le ciel descend ou reste en bas : il ne faut pas bouger. */
    public boolean isDangerous() {
        return time > FALL && time < FALL + DOWN + HOLD;
    }

    /** Le chiffre à afficher avant la chute : 3, 2, 1 ; 0 le reste du temps. */
    public int getCountdown() {
        return isWarning() ? (int) Math.ceil(FALL - time) : 0;
    }

    /** Hauteur (en pixels) du bord bas du ciel, pour le dessin. */
    public float getSkyBottom() {
        if (time < FALL) {
            return TOP;
        }
        if (time < FALL + DOWN) {
            return TOP - (TOP - lowest) * (time - FALL) / DOWN;
        }
        if (time < FALL + DOWN + HOLD) {
            return lowest;
        }
        float up = Math.min(1f, (time - FALL - DOWN - HOLD) / UP);
        return lowest + (TOP - lowest) * up;
    }

    private boolean playerMoves() {
        return Math.abs(target.getVx()) > 0f || Math.abs(target.getVy()) > 1f;
    }

    @Override
    public void onPlayerContact(Player player, EventBus bus) {
        if (isDangerous() && playerMoves()) {
            bus.publish(GameEvent.PLAYER_DIED);
            state = State.DONE;
        }
    }
}