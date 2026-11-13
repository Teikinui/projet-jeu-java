package com.teikinui.game.view;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.app.InputHandler;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelManager;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.CollisionSystem;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.GameListener;
import com.teikinui.game.model.GameSession;
import com.teikinui.game.model.GameTimer;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;

public final class PlayScreen extends BaseScreen implements GameListener {
    private static final float MAX_STEP = 1f / 30f;
    private final GameSession session;
    private final GameTimer timer;
    private final LevelManager levels;
    private final EventBus bus = new EventBus();
    private final InputHandler input = new InputHandler();
    private final CollisionSystem collisionSystem = new CollisionSystem();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Hud hud;

    private LevelTheme theme;
    private Level level;
    private Player player;
    private List<Trap> traps;
    private Goal goal;
    private boolean died;
    private boolean goalReached;

    public PlayScreen(TripTrapGame game, GameSession session, GameTimer timer, LevelManager levels) {
        super(game);
        this.session = session;
        this.timer = timer;
        this.levels = levels;
        bus.subscribe(session);
        bus.subscribe(levels);
        bus.subscribe(this);
        hud = new Hud(game.getAssets().getFont(), timer, session, levels);
        loadLevel();
    }

    private void loadLevel() {
        theme = levels.getCurrent();
        String text = Gdx.files.internal("assets/" + theme.getFileName()).readString();
        level = LevelLoader.fromString(text);
        player = new Player(level.getStartX(), level.getStartY(),
                GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE, GameConfig.RUN_SPEED);
        traps = level.createTraps(player);
        goal = level.createGoal();
        session.setCurrentLevel(theme.ordinal());
        setBackground(theme.getBackgroundColor());
    }

    @Override public void onEvent(GameEvent event) {
        if (event == GameEvent.PLAYER_DIED) died = true;
        else if (event == GameEvent.GOAL_REACHED) goalReached = true;
    }

    static boolean shouldRestartAfterDeath(boolean died, boolean goalReached) {
        return died && !goalReached;
    }

    @Override public void update(float dt) {
        float step = Math.min(dt, MAX_STEP);
        timer.update(step);
        if (timer.isTimeUp()) {
            bus.publish(GameEvent.TIME_UP);
            game.showTimeUp();
            return;
        }

        player.update(step, input);
        collisionSystem.move(player, level, step);
        
        for (Trap trap : traps) {
            trap.update(step);
        }
        collisionSystem.checkContacts(player, traps, bus);
        collisionSystem.checkContacts(player, List.of(goal), bus);

        // Chute mortelle
        if (player.getY() < 0) bus.publish(GameEvent.PLAYER_DIED);

        if (goalReached) {
            goalReached = false;
            died = false;
            if (levels.isFinished()) game.showVictory();
            else loadLevel();
        } else if (shouldRestartAfterDeath(died, goalReached)) {
            died = false;
            loadLevel();
        }
    }

    @Override public void draw(SpriteBatch batch) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // 1. LE DÉCOR (Murs, Sol, Trous)
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getCols(); col++) {
                char c = level.getTrapLetter(col, row);
                if (level.isWall(col, row)) {
                    shapeRenderer.setColor(0.62f, 0.16f, 0.12f, 1f); // Murs rouges
                } else if (c == 'v') {
                    shapeRenderer.setColor(0f, 0f, 0f, 1f); // Faux trous = NOIR
                } else {
                    shapeRenderer.setColor(0.82f, 0.64f, 0.36f, 1f); // Sol marron
                }
                shapeRenderer.rect(col * Level.TILE, row * Level.TILE, Level.TILE, Level.TILE);
            }
        }

        // 2. LES PIÈGES
        for (Trap trap : traps) {
            if (trap.isVisible()) {
                String name = trap.getClass().getSimpleName();
                if (name.contains("SkyFall")) {
                    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f); // Gris (Ciel)
                } else if (name.contains("Wave")) {
                    shapeRenderer.setColor(0.2f, 0.2f, 0.8f, 1f); // Bleu (Mur)
                } else if (name.contains("FakePalm")) {
                    shapeRenderer.setColor(1f, 0.8f, 0f, 1f); // Jaune (Fausse porte)
                } else if (name.contains("Spike")) {
                    shapeRenderer.setColor(1f, 1f, 1f, 1f); // Blanc (Pics)
                } else if (name.contains("HiddenDalle")) {
                    shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 0.5f); // Gris transparent (Dalle invisible)
                } else {
                    shapeRenderer.setColor(0.85f, 0.75f, 0.2f, 1f); // Défaut
                }
                shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
            }
        }

        // 3. PORTE & JOUEUR
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(goal.getX(), goal.getY(), goal.getWidth(), goal.getHeight());
        shapeRenderer.setColor(1f, 0.44f, 0.38f, 1f);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        
        shapeRenderer.end();
        batch.begin();
        hud.draw(batch);
        batch.end();
    }

    @Override public void dispose() { shapeRenderer.dispose(); }
}