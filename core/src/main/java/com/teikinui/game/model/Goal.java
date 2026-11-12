package com.teikinui.game.model;

/** L'objet à attraper à la fin d'un pays (palmier, bol de ramen, piment). */
public final class Goal extends Entity implements Interactable {
    private boolean reached;

    public Goal(float x, float y) {
        super(x, y, GameConfig.TILE, GameConfig.TILE);
    }

    /** L'objectif ne bouge pas. */
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
