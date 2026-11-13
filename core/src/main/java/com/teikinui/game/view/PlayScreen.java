package com.teikinui.game.view;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.app.InputHandler;
import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.LevelManager;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.level.TahitiTrapPlacement;
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
import com.teikinui.game.model.traps.ChargingWall;
import com.teikinui.game.model.traps.Croissant;
import com.teikinui.game.model.traps.Dervish;
import com.teikinui.game.model.traps.FakeChili;
import com.teikinui.game.model.traps.FakeSlab;
import com.teikinui.game.model.traps.FallingBlock;
import com.teikinui.game.model.traps.GrowingSpike;
import com.teikinui.game.model.traps.GoalSpike;
import com.teikinui.game.model.traps.HiddenSlab;
import com.teikinui.game.model.traps.Icicle;
import com.teikinui.game.model.traps.InvisibleBlock;
import com.teikinui.game.model.traps.Loukoum;
import com.teikinui.game.model.traps.Monkey;
import com.teikinui.game.model.traps.PopSpike;
import com.teikinui.game.model.traps.SimitVendor;
import com.teikinui.game.model.traps.SkyFall;
import com.teikinui.game.model.traps.Snowball;
import com.teikinui.game.model.traps.SpikeFloor;
import com.teikinui.game.model.traps.TiltSlab;
import com.teikinui.game.model.traps.Trap;

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
    private final Random random = new Random();
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
        if (theme != previous) {
            timer.reset();
        }
        String text = Gdx.files.internal("assets/" + theme.getFileName()).readString();
        level = LevelLoader.fromString(text);
        level.setAttempt(attempt);
        player = new Player(level.getStartX(), level.getStartY(),
                GameConfig.PLAYER_SIZE, GameConfig.PLAYER_SIZE, GameConfig.RUN_SPEED);
        player.setIcy(theme.isIcy());
        traps = level.createTraps(player);
        goal = level.createGoal();
        if (theme == LevelTheme.TAHITI) {
            traps = TahitiTrapPlacement.addThree(level, player, traps, random);
        }
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Échap : on quitte le niveau et on revient au menu.
            game.showMenu();
            return;
        }
        float step = Math.min(dt, MAX_STEP);
        timer.update(step);
        if (timer.isTimeUp()) {
            bus.publish(GameEvent.TIME_UP);
            game.showTimeUp();
            return;
        }

        player.update(step, input);
        collisionSystem.move(player, level, step);
        moveTahitiGoalNearPlayer(theme, player, goal, level.getStartX(), level.getStartY(), level, traps);
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

    /** À une case (écart entre rectangles) de G, l'objectif de Tahiti rejoint P une fois. */
    static boolean moveTahitiGoalNearPlayer(LevelTheme theme, Player player, Goal goal,
                                            float startX, float startY) {
        if (theme != LevelTheme.TAHITI || goal.isReached()) return false;
        float gapX = Math.max(0f, Math.max(goal.getX() - (player.getX() + player.getWidth()),
                player.getX() - (goal.getX() + goal.getWidth())));
        float gapY = Math.max(0f, Math.max(goal.getY() - (player.getY() + player.getHeight()),
                player.getY() - (goal.getY() + goal.getHeight())));
        return gapX <= GameConfig.TILE && gapY <= GameConfig.TILE
                && goal.moveToStart(startX, startY);
    }

    /** Embuscade créée seulement après que G a quitté sa position initiale. */
    static boolean moveTahitiGoalNearPlayer(LevelTheme theme, Player player, Goal goal,
                                            float startX, float startY, Level level, List<Trap> traps) {
        float oldX = goal.getX();
        float oldY = goal.getY();
        if (!moveTahitiGoalNearPlayer(theme, player, goal, startX, startY)) return false;
        int row = (int)(oldY / Level.TILE);
        int goalCol = (int)(oldX / Level.TILE);
        int launchCol = -1;
        for (int col = 0; col < goalCol; col++) {
            if (level.getTrapLetter(col, row) == 's') launchCol = col;
        }
        if (launchCol >= 0) {
            for (int col = launchCol + 1; col < goalCol; col++) {
                GoalSpike spike = new GoalSpike(col * Level.TILE, row * Level.TILE, player);
                spike.setLevel(level);
                traps.add(spike);
            }
        }
        return true;
    }

    @Override
    public void draw(SpriteBatch batch) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawLevel();
        drawTraps();
        if (theme.hidesGoal()) {
            // Chine : la vraie sortie ressemble à un trou à pics comme les autres.
            drawSpikes(goal.getX(), goal.getY(), goal.getWidth(), 20f);
        } else {
            setColor(theme.getGoalColor());
            shapeRenderer.rect(goal.getX(), goal.getY(), goal.getWidth(), goal.getHeight());
        }
        if (player.isBoosted()) {
            // Boosté par le croissant : le joueur devient doré.
            shapeRenderer.setColor(1f, 0.80f, 0.25f, 1f);
        } else {
            shapeRenderer.setColor(1f, 0.44f, 0.38f, 1f);
        }
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
                }
            }
        }
    }


    private void drawTraps() {
    for (Trap trap : traps) {
        if (!trap.isVisible()) {
            continue;
        }
         if (trap instanceof FallingBlock || trap instanceof InvisibleBlock) {
                setColor(theme.getWallColor());
                shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof Cactus || trap instanceof PopSpike || trap instanceof SpikeFloor || trap instanceof GoalSpike) {
             drawSpikes(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof GrowingSpike) {
             drawPillar(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof FakeChili) {
             // Dessiné exactement comme le vrai objectif : c'est tout le piège.
             setColor(theme.getGoalColor());
             shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof Monkey monkey) {
             drawMonkey(monkey);
        } else if (trap instanceof Croissant croissant) {
             drawPainting(croissant);
        } else if (trap instanceof Dervish dervish) {
             drawDervish(dervish);
        } else if (trap instanceof SimitVendor vendor) {
             drawSimitVendor(vendor);
        } else if (trap instanceof SkyFall sky) {
             // Le ciel : une bande sombre qui descend depuis le haut de l'écran.
             shapeRenderer.setColor(0.30f, 0.05f, 0.08f, 1f);
             float top = Level.TILE * level.getRows();
             shapeRenderer.rect(0f, sky.getSkyBottom(), Level.TILE * level.getCols(), top - sky.getSkyBottom());
        } else if (trap instanceof ChargingWall) {
             shapeRenderer.setColor(0.35f, 0.08f, 0.06f, 1f);
             shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
             shapeRenderer.setColor(0.95f, 0.80f, 0.25f, 1f);
             shapeRenderer.rect(trap.getX(), trap.getY(), 4f, trap.getHeight());
        } else if (trap instanceof HiddenSlab slab) {
             if (!slab.isRevealed()) {
                 // Tant qu'elle n'est pas trouvée, on la recouvre avec la couleur du fond.
                 setColor(theme.getBackgroundColor());
                 shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
             }
        } else if (trap instanceof FakeSlab) {
             setColor(theme.getWallColor());
             shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        } else if (trap instanceof TiltSlab tiltSlab) {
             if (tiltSlab.getTilt() > 0f) {
                 // Penchée : chaque case est dessinée un peu plus bas que sa voisine de gauche.
                 setColor(theme.getWallColor());
                 float drop = tiltSlab.getIndex() * 14f * tiltSlab.getTilt();
                 shapeRenderer.rect(trap.getX(), trap.getY() - drop, trap.getWidth(), trap.getHeight());
             }
        } else if (trap instanceof Icicle) {
             // Stalactite de glace : un triangle bleu clair pointé vers le bas.
             shapeRenderer.setColor(0.75f, 0.92f, 1f, 1f);
             shapeRenderer.triangle(trap.getX(), trap.getY() + trap.getHeight(),
                     trap.getX() + trap.getWidth(), trap.getY() + trap.getHeight(),
                     trap.getX() + trap.getWidth() / 2f, trap.getY());
        } else if (trap instanceof Snowball) {
             shapeRenderer.setColor(0.97f, 0.98f, 1f, 1f);
             float r = trap.getWidth() / 2f;
             shapeRenderer.circle(trap.getX() + r, trap.getY() + r, r);
        } else if (trap instanceof Loukoum) {
             shapeRenderer.setColor(0.95f, 0.55f, 0.70f, 1f);
             shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
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

    /** Tableau doré accroché au mur, avec le croissant dedans tant qu'il n'est pas mangé. */
    private void drawPainting(Croissant croissant) {
        float x = croissant.getX() - 13f;
        float y = croissant.getY() - 12f;
        shapeRenderer.setColor(0.85f, 0.68f, 0.25f, 1f);
        shapeRenderer.rect(x, y, 48f, 40f);
        shapeRenderer.setColor(0.20f, 0.25f, 0.35f, 1f);
        shapeRenderer.rect(x + 5f, y + 5f, 38f, 30f);
        if (!croissant.isEaten()) {
            shapeRenderer.setColor(0.90f, 0.60f, 0.20f, 1f);
            shapeRenderer.rect(croissant.getX(), croissant.getY(), croissant.getWidth(), croissant.getHeight());
        }
    }

    /** Derviche : longue robe blanche qui tourne (elle s'élargit et se resserre) + chapeau. */
    private void drawDervish(Dervish dervish) {
        float x = dervish.getX();
        float y = dervish.getY();
        float w = dervish.getWidth();
        float open = 4f * (float) Math.abs(Math.sin(Math.toRadians(dervish.getSpin())));
        shapeRenderer.setColor(0.95f, 0.95f, 0.92f, 1f);
        shapeRenderer.triangle(x - open, y, x + w + open, y, x + w / 2f, y + 22f);
        shapeRenderer.setColor(0.55f, 0.35f, 0.25f, 1f);
        shapeRenderer.rect(x + w / 2f - 4f, y + 20f, 8f, 10f);
    }

    /** Vendeur de simits : un corps rouge + le simit (anneau brun) qu'il lance. */
    private void drawSimitVendor(SimitVendor vendor) {
        shapeRenderer.setColor(0.75f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(vendor.getHomeX() + 6f, vendor.getHomeY(), 20f, 28f);
        shapeRenderer.setColor(0.70f, 0.45f, 0.20f, 1f);
        shapeRenderer.rect(vendor.getX(), vendor.getY(), vendor.getWidth(), vendor.getHeight());
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