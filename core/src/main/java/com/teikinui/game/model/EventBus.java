package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private static final List<GameListener> listeners = new ArrayList<>();

    public static void subscribe(GameListener listener) {
        listeners.add(listener);
    }

    public static void publish(GameEvent event) {
        for (GameListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}