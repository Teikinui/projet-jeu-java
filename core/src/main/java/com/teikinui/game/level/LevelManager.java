package com.teikinui.game.level;

import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.GameListener;

/**
 * Connaît l'ordre des pays et le pays en cours. Quand l'objectif est atteint,
 * il passe au pays suivant ; après le dernier, le voyage est terminé.
 */
public final class LevelManager implements GameListener {
    private LevelTheme current = LevelTheme.values()[0];
    private boolean finished;

    @Override
    public void onEvent(GameEvent event) {
        if (event != GameEvent.GOAL_REACHED || finished) {
            return;
        }
        LevelTheme next = current.next();
        if (next == null) {
            finished = true;
        } else {
            current = next;
        }
    }

    public LevelTheme getCurrent() {
        return current;
    }

    /** Vrai quand l'objectif du dernier pays a été attrapé. */
    public boolean isFinished() {
        return finished;
    }
}