package com.teikinui.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.teikinui.game.level.LevelManager;
import com.teikinui.game.model.GameSession;
import com.teikinui.game.model.GameTimer;
import com.teikinui.game.view.Assets;
import com.teikinui.game.view.Hud;
import com.teikinui.game.view.MenuScreen;
import com.teikinui.game.view.MessageScreen;
import com.teikinui.game.view.PlayScreen;

/**
 * Point de départ du jeu. Regroupe toute la navigation entre les écrans :
 * showMenu(), startGame(), showTimeUp(), showVictory().
 */
public class TripTrapGame extends Game {
    private SpriteBatch batch;
    private Assets assets;
    private GameSession session;
    private GameTimer timer;
    private PlayScreen playScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        showMenu();
    }

    /** Le hall d'aéroport. */
    public void showMenu() {
        disposePlayScreen();
        setScreen(new MenuScreen(this));
    }

    /** Une nouvelle partie : chrono à 8 minutes, 0 mort, premier pays. */
    public void startGame() {
        disposePlayScreen();
        session = new GameSession();
        timer = new GameTimer();
        playScreen = new PlayScreen(this, session, timer, new LevelManager());
        setScreen(playScreen);
    }

    /** Le chrono est à zéro : écran de fin avec le nombre de morts. */
    public void showTimeUp() {
        String details = "Tu es mort " + session.getDeathCount() + " fois en 8 minutes.\n"
                + "Le voyage, ce n'est clairement pas pour toi.";
        setScreen(new MessageScreen(this, "TEMPS ÉCOULÉ", details, 0f, this::showMenu));
    }

    /** Le dernier objectif est attrapé : écran de victoire. */
    public void showVictory() {
        String details = "Tu as traversé Tahiti, la Chine et la Tunisie.\n"
                + "Morts : " + session.getDeathCount()
                + "     Temps restant : " + Hud.formatTime(timer.getRemaining());
        setScreen(new MessageScreen(this, "BON VOYAGE !", details, 0f, this::showMenu));
    }

    private void disposePlayScreen() {
        if (playScreen != null) {
            playScreen.dispose();
            playScreen = null;
        }
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Assets getAssets() {
        return assets;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null && getScreen() != playScreen) {
            getScreen().dispose();
        }
        disposePlayScreen();
        assets.dispose();
        batch.dispose();
    }
}