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
import com.teikinui.game.model.PlayerInput;
import com.teikinui.game.model.ReversedInput;
import com.teikinui.game.model.traps.Cactus;
import com.teikinui.game.model.traps.FakeChili;
import com.teikinui.game.model.traps.FallingBlock;
import com.teikinui.game.model.traps.GrowingSpike;
import com.teikinui.game.model.traps.InvisibleBlock;
import com.teikinui.game.model.traps.Monkey;
import com.teikinui.game.model.traps.PopSpike;
import com.teikinui.game.model.traps.SpikeFloor;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.TrapType;

/**
 * La partie : relie le niveau, le chrono, les collisions et l'affichage.
 * La caméra ne bouge pas : chaque pays tient entièrement dans l'écran.
 */
public final class PlayScreen extends BaseScreen implements GameListener {
    private static final float MAX_STEP = 1f / 30f;

    private final GameSession session;
    private final GameTimer timer;
    private final LevelManager levels;
    private final EventBus bus = new EventBus();
    private final InputHandler keyboard = new InputHandler();
    private final CollisionSystem collisionSystem = new CollisionSystem();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Hud hud;

    private LevelTheme theme;
    private PlayerInput input;
    private int attempt;
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

    /** (Re)charge le pays en cours : le joueur revient au départ, les pièges sont remis à zéro. */
    private void loadLevel() {
        LevelTheme previous = theme;
        theme = levels.getCurrent();
        attempt = theme == previous ? attempt + 1 : 0;
        String text = Gdx.files.internal("assets/" + theme.getFileName()).readString();
        level = LevelLoader.fromString(text);
        level.setAttempt(attempt);
        player = new Player(level.getStartX(), level.getStartY(),
                GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE, GameConfig.RUN_SPEED);
        traps = level.createTraps(player);
        goal = level.createGoal();
        session.setCurrentLevel(theme.ordinal());
        setBackground(theme.getBackgroundColor());
        // Vietnam : on enveloppe le clavier pour échanger gauche et droite (Décorateur).
        input = theme.hasInvertedControls() ? new ReversedInput(keyboard) : keyboard;
    }

    /** On note seulement ce qui s'est passé ; on le traite à la fin de update(). */
    @Override
    public void onEvent(GameEvent event) {
        if (event == GameEvent.PLAYER_DIED) {
            died = true;
        } else if (event == GameEvent.GOAL_REACHED) {
            goalReached = true;
        }
    }

    /** La victoire prend priorité si un objectif et un piège sont touchés ensemble. */
    static boolean shouldRestartAfterDeath(boolean died, boolean goalReached) {
        return died && !goalReached;
    }

    @Override
    public void update(float dt) {
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
        collisionSystem.checkFall(player, bus);
        collisionSystem.checkContacts(player, List.of(goal), bus);

        if (goalReached) {
            goalReached = false;
            died = false;
            if (levels.isFinished()) {
                game.showVictory();
            } else {
                loadLevel();
            }
        } else if (shouldRestartAfterDeath(died, goalReached)) {
            died = false;
            loadLevel();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawLevel();
        drawTraps();
        setColor(theme.getGoalColor());
        shapeRenderer.rect(goal.getX(), goal.getY(), goal.getWidth(), goal.getHeight());
        shapeRenderer.setColor(1f, 0.44f, 0.38f, 1f);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        shapeRenderer.end();

        batch.begin();
        hud.draw(batch);
        batch.end();
    }

    private void drawLevel() {
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getCols(); col++) {
                if (level.isWall(col, row)) {
                    setColor(theme.getWallColor());
                    shapeRenderer.rect(col * Level.TILE, row * Level.TILE, Level.TILE, Level.TILE);
                } else if (isLaunchPoint(level.getTrapLetter(col, row))) {
                    shapeRenderer.setColor(0.3f, 0.5f, 0.65f, 1f);
                    shapeRenderer.rect(col * Level.TILE + 12f, row * Level.TILE + 12f, 8f, 8f);
                }
            }
        }
    }

    /** Petit repère sous les pièges qui tombent ou qui sont lancés (sans image pour l'instant). */
    private boolean isLaunchPoint(char letter) {
        return letter == TrapType.COCONUT.getLetter()
                || letter == TrapType.CLOUD_SHARK.getLetter()
                || letter == TrapType.STARFISH.getLetter();
    }


    private void drawTraps() {
    for (Trap trap : traps) {
        if (!trap.isVisible()) {
            continue;
        }
         if (trap instanceof FallingBlock || trap instanceof InvisibleBlock) {
                setColor(theme.getWallColor());
                shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof Cactus || trap instanceof PopSpike || trap instanceof SpikeFloor) {
             drawSpikes(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof GrowingSpike) {
             drawPillar(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof FakeChili) {
             // Dessiné exactement comme le vrai objectif : c'est tout le piège.
             setColor(theme.getGoalColor());
             shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof Monkey monkey) {
             drawMonkey(monkey);
        } else {
            shapeRenderer.setColor(0.85f, 0.75f, 0.2f, 1f);
            shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        }
    }
    }

    /** Deux pics blancs qui sortent du sol. */
    private void drawSpikes(float x, float y, float width, float height) {
        shapeRenderer.setColor(0.95f, 0.95f, 0.9f, 1f);
        float half = width / 2f;
        shapeRenderer.triangle(x, y, x + half, y, x + half / 2f, y + height * 0.8f);
        shapeRenderer.triangle(x + half, y, x + width, y, x + half * 1.5f, y + height * 0.8f);
    }

    /** Singe : un corps marron sous le plafond + le fruit vert qu'il lance. */
    private void drawMonkey(Monkey monkey) {
        shapeRenderer.setColor(0.45f, 0.30f, 0.18f, 1f);
        shapeRenderer.rect(monkey.getHomeX() + 6f, monkey.getHomeY() + 10f, 20f, 20f);
        shapeRenderer.setColor(0.85f, 0.70f, 0.55f, 1f);
        shapeRenderer.rect(monkey.getHomeX() + 11f, monkey.getHomeY() + 14f, 10f, 8f);
        shapeRenderer.setColor(0.55f, 0.75f, 0.20f, 1f);
        shapeRenderer.rect(monkey.getX(), monkey.getY(), monkey.getWidth(), monkey.getHeight());
    }

    /** Pilier de pics : deux barres blanches côte à côte (comme dans la vidéo). */
    private void drawPillar(float x, float y, float width, float height) {
        shapeRenderer.setColor(0.95f, 0.95f, 0.9f, 1f);
        float bar = width / 3f;
        shapeRenderer.rect(x, y, bar, height);
        shapeRenderer.rect(x + 2 * bar, y, bar, height);
    }

    private void setColor(float[] rgb) {
        shapeRenderer.setColor(rgb[0], rgb[1], rgb[2], 1f);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}