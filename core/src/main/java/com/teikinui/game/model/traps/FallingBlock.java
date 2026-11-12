package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Bloc qui tombe puis devient un mur (patron Template Method) :
 * cette classe gère la chute et l'atterrissage, chaque sous-classe
 * décide seulement QUAND le bloc se décroche (shouldFall).
 */
public abstract class FallingBlock extends Trap {
    protected final int col;
    protected final int row;
    private final float fallSpeed;
    private boolean detached;

    protected FallingBlock(float x, float y, Player target, float fallSpeedInTiles) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        col = Math.round(x / GameConfig.TILE);
        row = Math.round(y / GameConfig.TILE);
        fallSpeed = fallSpeedInTiles * GameConfig.TILE;
        setVisible(false);
    }

    /** Vrai quand le bloc doit se décrocher. */
    protected abstract boolean shouldFall();

    /** Vrai si le bloc s'est déjà décroché (utile pour les blocs voisins). */
    public boolean isDetached() {
        return detached;
    }

    @Override
    protected final boolean isTriggered() {
        return shouldFall();
    }

    @Override
    protected void activate(float dt) {
        if (!detached) {
            detached = true;
            level.setWall(col, row, false);
            setVisible(true);
        }
        y -= fallSpeed * dt;
        int belowRow = (int) Math.floor((y - 1f) / GameConfig.TILE);
        float floorTop = (belowRow + 1) * GameConfig.TILE;
        if (level.isWall(col, belowRow) && y <= floorTop) {
            y = floorTop;
            if (collidesWith(target)) {
                return;
            }
            level.setWall(col, belowRow + 1, true);
            state = State.DONE;
        }
    }
}