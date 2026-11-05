package com.teikinui.game.model;

public abstract class Trap extends Entity{
    public enum State {
        WAITING,
        TRIGGERED,
        DONE
    }

    protected State state = State.WAITING;
    protected abstract boolean isTriggered();
    protected abstract void activate(float dt);

    public Trap(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override 
    public void update(float dt) {
        if (state == State.WAITING && isTriggered()) {
            state = State.TRIGGERED;
        }

        if (state == State.TRIGGERED) {
            activate(dt);
        }
    }
}
