package com.teikinui.game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/** Point d'entrée du jeu. Ajoute ici les écrans et les règles de ton jeu. */
public class TripTrapGame extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private float playerX = 100f;
    private float playerY = 100f;
    private static final float PLAYER_SIZE = 32f;


    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        Gdx.app.log("TripTrapGame", "Le jeu démarre !");
    }

    @Override
    public void render() {

        float movement = 200f * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= movement;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerY += movement;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += movement;
        }

        Gdx.gl.glClearColor(0.10f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
        shapeRenderer.end();

        
    }
}
