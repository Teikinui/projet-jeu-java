package com.teikinui.game.model.traps;

import com.teikinui.game.model.Entity;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.Interactable;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.triggers.Trigger;

/** Base pour les pièges déclenchés par une entité du jeu. */
public abstract class Trap extends Entity implements Interactable {
    public enum State {
        WAITING,
        TRIGGERED,
        DONE
    }

    protected State state = State.WAITING;
    protected final Player target;
    protected float startX;
    protected float startY;
    protected Level level;
    private Trigger trigger;
    private boolean visible = true;
    private boolean marked = true;

    protected Trap(float x, float y, float width, float height, Player target) {
        super(x, y, width, height);
        this.target = target;
    }

    public void setStart(float x, float y) {
        startX = x;
        startY = y;
    }

    /** Le niveau où se trouve le piège : il peut y ajouter ou enlever des murs. */
    public void setLevel(Level level) {
        this.level = level;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public boolean isVisible() {
        return visible && state != State.DONE;
    }

    /** Seuls les pièges déjà déclenchés peuvent blesser (ou téléporter). */
    public boolean isActive() {
        return state == State.TRIGGERED;
    }

    /** Le rendu peut dessiner un indice de la case même avant déclenchement. */
    public boolean isMarked() {
        return marked && state != State.DONE;
    }

    protected void setMarked(boolean marked) {
        this.marked = marked;
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected boolean triggeredByPlayer() {
        return trigger != null && trigger.isTriggered(this, target);
    }

    protected abstract boolean isTriggered();

    protected abstract void activate(float deltaTime);

    @Override
    public final void update(float deltaTime) {
        if (state == State.WAITING && isTriggered()) {
            state = State.TRIGGERED;
        }
        if (state == State.TRIGGERED) {
            activate(deltaTime);
        }
    }

    /** Par défaut, un piège consommé ne peut pas tuer deux fois. */
    @Override
    public void onPlayerContact(Player player, EventBus bus) {
        if (isActive() && collidesWith(player)) {
            bus.publish(com.teikinui.game.model.GameEvent.PLAYER_DIED);
            state = State.DONE;
        }
    }

    public final State getState() {
        return state;
    }
}
