package com.teikinui.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/** Point d'entrée du jeu. Ajoute ici les écrans et les règles de ton jeu. */
public class MyGdxGame extends ApplicationAdapter {
    @Override
    public void create() {
        Gdx.app.log("MyGdxGame", "Le jeu démarre !");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.10f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
