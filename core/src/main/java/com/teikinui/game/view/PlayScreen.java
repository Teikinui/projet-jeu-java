package com.teikinui.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.teikinui.game.app.InputHandler;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.model.CollisionSystem;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;

/** Game screen: loads the level, moves the player and draws everything. */
public class PlayScreen implements Screen {
    private static final float VIEW_WIDTH = 960f;
    private static final float VIEW_HEIGHT = 540f;
    private static final float PLAYER_SIZE = 32f;
    private static final float PLAYER_SPEED = 200f;
    private static final int START_COL = 1;
    private static final int START_ROW = 2;

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
        player = new Player(START_COL * Level.TILE, START_ROW * Level.TILE,
                PLAYER_SIZE, PLAYER_SIZE, PLAYER_SPEED);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    private void update(float delta) {
        player.update(delta, input.isLeft(), input.isRight(), input.isJump());
        collisionSystem.move(player, level, delta);
        followPlayer();
    }

    private void followPlayer() {
        float levelWidth = level.getCols() * Level.TILE;
        float half = VIEW_WIDTH / 2f;
        float target = player.getX() + player.getWidth() / 2f;
        camera.position.x = MathUtils.clamp(target, half, Math.max(half, levelWidth - half));
        camera.position.y = VIEW_HEIGHT / 2f;
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
                    shapeRenderer.rect(col * Level.TILE, row * Level.TILE, Level.TILE, Level.TILE);
                }
            }
        }

        shapeRenderer.setColor(1f, 0.44f, 0.38f, 1f);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
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
        shapeRenderer.dispose();
    }
}