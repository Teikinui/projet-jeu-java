package com.teikinui.game.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.GameListener;

/**
 * Joue une phrase de moquerie du pays à chaque mort.
 * Abonné à l'EventBus (pattern Observer) : il réagit à PLAYER_DIED.
 * Fichiers : assets/sounds/death_<pays>_<n>.ogg, n = 0, 1, 2… sans trou.
 */
public final class SoundManager implements GameListener, Disposable {
    private final List<Sound> deathSounds = new ArrayList<>();
    private final Random random = new Random();
    private boolean muted;

    /** Charge toutes les phrases du pays (s'arrête au premier numéro manquant). */
    public void loadCountry(String themeKey) {
        dispose();
        for (int n = 0; ; n++) {
            FileHandle file = Gdx.files.internal("assets/sounds/death_" + themeKey + "_" + n + ".ogg");
            if (!file.exists()) {
                break;
            }
            deathSounds.add(Gdx.audio.newSound(file));
        }
    }

    /** Touche M : coupe ou remet le son. */
    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopAll();
        }
    }

    @Override
    public void onEvent(GameEvent event) {
        if (event != GameEvent.PLAYER_DIED || muted || deathSounds.isEmpty()) {
            return;
        }
        stopAll();
        deathSounds.get(random.nextInt(deathSounds.size())).play();
    }

    private void stopAll() {
        for (Sound sound : deathSounds) {
            sound.stop();
        }
    }

    @Override
    public void dispose() {
        for (Sound sound : deathSounds) {
            sound.dispose();
        }
        deathSounds.clear();
    }
}
