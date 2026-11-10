package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

/** Envoie les événements aux abonnés dans leur ordre d'inscription. */
public class EventBus {
    private final List<GameListener> listeners = new ArrayList<>();

    public void subscribe(GameListener listener) {
        listeners.add(listener);
    }

    public void publish(GameEvent event) {
        for (GameListener listener : List.copyOf(listeners)) {
            listener.onEvent(event);
        }
    }
}
