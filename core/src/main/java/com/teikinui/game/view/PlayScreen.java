package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.teikinui.game.CollisionSystem;
import com.teikinui.game.Level;
import com.teikinui.game.LevelLoader;
import com.teikinui.game.Player;
import com.teikinui.game.Tile;
import com.teikinui.game.app.InputHandler;

/** Écran de jeu qui charge le niveau, gère les commandes et dessine le monde. */
public final class PlayScreen implements Screen {
    private static final float VIEW_WIDTH = 960f;
    private static final float VIEW_HEIGHT = 540f;
    private static final float PLAYER_SIZE = 32f;
    private static final float PLAYER_SPEED = 200f;

    private final InputHandler input = new InputHandler();
    private final CollisionSystem collisionSystem = new CollisionSystem();
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Level level;
    private Player player;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        shapeRenderer = new ShapeRenderer();
        level = LevelLoader.fromString(Gdx.files.internal("assets/tahiti.txt").readString());
        player = createPlayerAtStart();
        followPlayer();
    }

    private Player createPlayerAtStart() {
        int startCol = 1;
        int startRow = 2;
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getCols(); col++) {
                if (level.getTile(col, row) == Tile.START) {
                    startCol = col;
                    startRow = row;
                    return new Player(startCol * Level.TILE, startRow * Level.TILE,
                            PLAYER_SIZE, PLAYER_SIZE, PLAYER_SPEED);
                }
            }
        }
        return new Player(startCol * Level.TILE, startRow * Level.TILE,
                PLAYER_SIZE, PLAYER_SIZE, PLAYER_SPEED);
    }

    @Override
    public void render(float delta) {
        player.update(delta, input.isLeft(), input.isRight(), input.isJump());
        collisionSystem.move(player, level, delta);
        followPlayer();
        draw();
    }

    private void followPlayer() {
        float halfViewport = camera.viewportWidth / 2f;
        float levelWidth = level.getCols() * Level.TILE;
        float targetX = player.getX() + player.getWidth() / 2f;
        camera.position.x = MathUtils.clamp(targetX, halfViewport,
                Math.max(halfViewport, levelWidth - halfViewport));
        camera.position.y = camera.viewportHeight / 2f;
        camera.update();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.10f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.55f, 0.40f, 0.25f, 1f);
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getCols(); col++) {
                if (level.isWall(col, row)) {
                    shapeRenderer.rect(col * Level.TILE, row * Level.TILE,
                            Level.TILE, Level.TILE);
                }
            }
        }
        shapeRenderer.setColor(1f, 0.44f, 0.38f, 1f);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0 || camera == null) {
            return;
        }
        camera.viewportWidth = VIEW_HEIGHT * width / height;
        camera.viewportHeight = VIEW_HEIGHT;
        followPlayer();
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
