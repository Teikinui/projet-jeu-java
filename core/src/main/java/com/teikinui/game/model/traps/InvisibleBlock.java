package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Bloc invisible (Sri Lanka) : on ne le voit pas. Quand le joueur saute
 * et que sa tête arrive dessous, il apparaît, devient un mur et le joueur
 * se cogne. Ensuite, on peut monter dessus comme sur une marche.
 */
public final class InvisibleBlock extends Trap {
    private final int col;
    private final int row;

    public InvisibleBlock(float x, float y, Player target) {
        super(x, y, GameConfig.TILE, GameConfig.TILE, target);
        col = Math.round(x / GameConfig.TILE);
        row = Math.round(y / GameConfig.TILE);
        setVisible(false);
        setMarked(false);
    }

    /** La tête du joueur, qui monte, touche le dessous du bloc. */
    @Override
    protected boolean isTriggered() {
        boolean below = target.getX() + target.getWidth() > x && target.getX() < x + width;
        float head = target.getY() + target.getHeight();
        return below && target.getVy() > 0f && target.getY() < y && head > y - 2f;
    }

    @Override
    protected void activate(float dt) {
        if (level.isWall(col, row)) {
            return;
        }
        level.setWall(col, row, true);
        setVisible(true);
        if (target.getY() + target.getHeight() > y) {
            // Bonk : on remet la tête juste sous le bloc et le joueur retombe.
            target.teleport(target.getX(), y - target.getHeight());
        }
    }

    /** Le bloc ne tue pas : il fait seulement tomber le joueur. */
    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}