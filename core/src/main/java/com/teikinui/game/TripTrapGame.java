package com.teikinui.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.CollisionSystem;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/** Point d'entrée du jeu. Ajoute ici les écrans et les règles de ton jeu. */
public class TripTrapGame extends ApplicationAdapter {
    private static final float PLAYER_SIZE = 32f;
    private ShapeRenderer shapeRenderer;
    private Player player;
    private Level level;
    private CollisionSystem collisionSystem;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        level = LevelLoader.fromString(Gdx.files.internal("assets/tahiti.txt").readString());
        collisionSystem = new CollisionSystem();
        player = new Player(100f, 100f, PLAYER_SIZE, PLAYER_SIZE, 200f);
        Gdx.app.log("TripTrapGame", "Le jeu démarre !");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        player.update(
            deltaTime,
            Gdx.input.isKeyPressed(Input.Keys.LEFT),
            Gdx.input.isKeyPressed(Input.Keys.RIGHT),
            Gdx.input.isKeyPressed(Input.Keys.UP)
        );
        // Player calcule vitesses et gravité; CollisionSystem applique le déplacement.
        collisionSystem.move(player, level, deltaTime);

        Gdx.gl.glClearColor(0.10f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
