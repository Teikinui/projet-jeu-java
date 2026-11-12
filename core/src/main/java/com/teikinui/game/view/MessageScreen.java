package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.model.GameConfig;

/**
 * Un message de fin de partie : chrono écoulé ou victoire.
 * Entrée lance ensuite l'action prévue.
 */
public final class MessageScreen extends BaseScreen {
    private static final float[] BACKGROUND = {0.25f, 0.05f, 0.08f};

    private final String title;
    private final String details;
    private final float duration;
    private final Runnable onEnd;
    private float elapsed;
    private boolean ended;

    public MessageScreen(TripTrapGame game, String title, String details,
                         float duration, Runnable onEnd) {
        super(game);
        this.title = title;
        this.details = details;
        this.duration = duration;
        this.onEnd = onEnd;
        setBackground(BACKGROUND);
    }

    private boolean waitsForEnter() {
        return duration <= 0f;
    }

    @Override
    public void update(float dt) {
        if (ended) {
            return;
        }
        elapsed += dt;
        boolean timeOver = !waitsForEnter() && elapsed >= duration;
        boolean enterPressed = waitsForEnter() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        if (timeOver || enterPressed) {
            ended = true;
            onEnd.run();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        int width = GameConfig.VIEW_WIDTH;
        batch.begin();
        game.getAssets().getTitleFont().draw(batch, title, 0, 360, width, Align.center, false);
        game.getAssets().getFont().draw(batch, details, 60, 260, width - 120, Align.center, true);
        if (waitsForEnter()) {
            game.getAssets().getFont().draw(batch, "Appuie sur Entrée pour revenir au menu",
                    0, 110, width, Align.center, false);
        }
        batch.end();
    }
}