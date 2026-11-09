package com.teikinui.game;

/** Base pour les pièges déclenchés par une entité du jeu. */
public abstract class Trap extends Entity {
    public enum State {
        WAITING,
        TRIGGERED,
        DONE
    }

    protected State state = State.WAITING;

    protected Trap(float x, float y, float width, float height) {
        super(x, y, width, height);
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

    public final State getState() {
        return state;
    }
}
