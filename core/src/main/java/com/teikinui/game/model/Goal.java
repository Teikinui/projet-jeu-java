package com.teikinui.game.model;

/** L'objet à attraper à la fin d'un pays (palmier, bol de ramen, piment). */
public final class Goal extends Entity implements Interactable {
    private boolean reached;
    private boolean movedToStart;

    public Goal(float x, float y) {
        super(x, y, GameConfig.TILE, GameConfig.TILE);
    }

    /** Déplace une seule fois l'objectif au départ sans déplacer le joueur. */
    public boolean moveToStart(float startX, float startY) {
        if (reached || movedToStart) return false;
        x = startX;
        y = startY;
        movedToStart = true;
        return true;
    }

    /** L'objectif ne bouge pas lors de la mise à jour par frame. */
    @Override
    public void update(float deltaTime) {
    }

    /** Au premier contact avec le joueur, annonce que le pays est terminé. */
    @Override
    public void onPlayerContact(Player player, EventBus bus) {
        if (!reached && collidesWith(player)) {
            reached = true;
            bus.publish(GameEvent.GOAL_REACHED);
        }
    }

    public boolean isReached() {
        return reached;
    }
}
