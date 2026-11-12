package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Updatable;

/**
 * Base de tous les écrans. La caméra est fixe et montre exactement
 * GameConfig.VIEW_WIDTH x GameConfig.VIEW_HEIGHT pixels du monde.
 * render() est écrit une seule fois ici (patron Template Method) :
 * il met à jour l'écran, efface, puis le dessine.
 */
public abstract class BaseScreen implements Screen, Updatable, Drawable {
    protected final TripTrapGame game;
    protected final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport =
            new FitViewport(GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT, camera);
    private float[] background = {0f, 0f, 0f};

    protected BaseScreen(TripTrapGame game) {
        this.game = game;
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public final void render(float delta) {
        update(delta);
        ScreenUtils.clear(background[0], background[1], background[2], 1f);
        viewport.apply();
        camera.update();
        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(camera.combined);
        draw(batch);
    }

    /** Couleur du fond : rouge, vert, bleu entre 0 et 1. */
    protected void setBackground(float[] rgb) {
        background = rgb.clone();
    }

    /** Si la fenêtre change de taille, l'image s'adapte sans se déformer. */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}