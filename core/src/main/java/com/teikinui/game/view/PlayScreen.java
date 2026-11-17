package com.teikinui.game.view;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
import com.teikinui.game.model.traps.projectile.Starfish;

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
    private List<TahitiTrapPlacement.Placement> tahitiPlan;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Hud hud;
    private final TauntBubble taunt;
    private final Color haloColor = new Color();
    private ThemeStyle style;
    private String themeKey;
    private PlayerSprites playerSprites;
    private PlayerSprites.State spriteState;
    private float stateTime;
    private float deathTime;
    private boolean facingLeft;

    private LevelTheme theme;
    private PlayerInput input;
    private int attempt;
    private Level level;
    private Player player;
    private List<Trap> traps;
    private List<Trap> tahitiReturnWave = List.of();
    private Goal goal;
    private boolean died;
    private boolean goalReached;
    private boolean leaving;

    public PlayScreen(TripTrapGame game, GameSession session, GameTimer timer, LevelManager levels) {
        super(game);
        this.session = session;
        this.timer = timer;
        this.levels = levels;
        bus.subscribe(session);
        bus.subscribe(levels);
        bus.subscribe(this);
        hud = new Hud(game.getAssets(), timer, session, levels);
        taunt = new TauntBubble(game.getAssets());
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
        if (theme != previous) {
            style = ThemeStyle.of(theme);
            themeKey = theme.name().toLowerCase(java.util.Locale.ROOT).replace("_", "");
            playerSprites = new PlayerSprites(game.getAssets(), themeKey);
            taunt.show(style, 3f);
        }
        spriteState = null;
        stateTime = 0f;
        deathTime = 0f;
        facingLeft = false;
        traps = level.createTraps(player);
        goal = level.createGoal();
        tahitiReturnWave = List.of();
        if (theme == LevelTheme.TAHITI) {
            if (tahitiPlan == null) tahitiPlan = TahitiTrapPlacement.plan(level, traps, random);
            traps = TahitiTrapPlacement.instantiate(level, player, traps, tahitiPlan);
            // Prépare des objets neufs au respawn, hors rendu et collisions avant G→P.
            tahitiReturnWave = TahitiTrapPlacement.returnWave(level, player);
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
            if (!died) {
                deathTime = 0f;
                changeSpriteState(PlayerSprites.State.DIE);
                taunt.show(style, 2f);
            }
            died = true;
        } else if (event == GameEvent.GOAL_REACHED) {
            goalReached = true;
        }
    }

    /** La victoire prend priorité si un objectif et un piège sont touchés ensemble. */
    static boolean shouldRestartAfterDeath(boolean died, boolean goalReached) {
        return died && !goalReached;
    }

    /** Attend la fin du sprite de mort s'il existe, sans bloquer le fallback carré. */
    static boolean shouldRespawnAfterDeath(boolean died, boolean goalReached,
                                           float duration, float elapsed) {
        return shouldRestartAfterDeath(died, goalReached) && elapsed >= duration;
    }

    private void changeSpriteState(PlayerSprites.State next) {
        if (spriteState != next) {
            spriteState = next;
            stateTime = 0f;
        }
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Échap : on quitte le niveau et on revient au menu.
            leaving = true;
            game.showMenu();
            return;
        }
        float step = Math.min(dt, MAX_STEP);
        taunt.update(step);
        timer.update(step);
        if (timer.isTimeUp()) {
            bus.publish(GameEvent.TIME_UP);
            game.showTimeUp();
            return;
        }

        if (died) {
            deathTime += step;
            stateTime += step;
        } else {
            player.update(step, input);
            collisionSystem.move(player, level, step);
            activateTahitiReturnWave(theme, player, goal, level, traps, tahitiReturnWave);
            updateTraps(theme, level, goal, traps, step);
            collisionSystem.checkContacts(player, traps, bus);
            collisionSystem.checkFall(player, bus);
            collisionSystem.checkContacts(player, List.of(goal), bus);
            if (!died) {
                if (player.getVx() < -0.01f) facingLeft = true;
                else if (player.getVx() > 0.01f) facingLeft = false;
                changeSpriteState(PlayerSprites.stateFor(player.getVx(), player.isOnGround(), false));
                stateTime += step;
            }
        }

        if (goalReached) {
            goalReached = false;
            died = false;
            if (levels.isFinished()) {
                game.showVictory();
            } else {
                loadLevel();
            }
        } else if (shouldRespawnAfterDeath(died, goalReached,
                playerSprites.getDuration(PlayerSprites.State.DIE), deathTime)) {
            died = false;
            loadLevel();
        }
    }

    /** La vague arrive après G→P au premier essai, puis est réarmée dès le respawn. */
    static boolean activateTahitiReturnWave(LevelTheme theme, Player player, Goal goal,
                                            Level level, List<Trap> traps, List<Trap> wave) {
        if (theme != LevelTheme.TAHITI) return false;
        boolean goalMoved = moveTahitiGoalNearPlayer(theme, player, goal,
                level.getStartX(), level.getStartY());
        boolean waveAdded = level.getAttempt() > 0 && !goal.hasMovedToStart()
                && addMissingReturnWave(traps, wave);
        if (goal.hasMovedToStart()) {
            waveAdded |= addMissingReturnWave(traps, wave);
        }
        return goalMoved || waveAdded;
    }

    /** Réinsère uniquement les nouveaux objets absents, sans dupliquer la vague. */
    private static boolean addMissingReturnWave(List<Trap> traps, List<Trap> wave) {
        boolean added = false;
        for (Trap trap : wave) {
            if (traps.stream().noneMatch(existing -> existing == trap)) {
                traps.add(trap);
                added = true;
            }
        }
        return added;
    }

    /** L'étoile qui garde G à Tahiti se déclenche après le départ de G, pas avant. */
    static void updateTraps(LevelTheme theme, Level level, Goal goal, List<Trap> traps, float step) {
        for (Trap trap : traps) {
            if (theme == LevelTheme.TAHITI && trap instanceof Starfish
                    && !goal.hasMovedToStart()
                    && trap.getX() == goal.getX() - 4f * Level.TILE
                    && trap.getY() == goal.getY()
                    && level.getTrapLetter((int)(trap.getX() / Level.TILE),
                            (int)(trap.getY() / Level.TILE)) == 's') continue;
            trap.update(step);
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

    @Override
    public void draw(SpriteBatch batch) {
        if (leaving) {
            // L'écran vient d'être fermé par Échap : son ShapeRenderer est détruit.
            return;
        }
        Assets assets = game.getAssets();
        batch.begin();
        String backgroundName = "bg_" + themeKey;
        if (assets.hasTexture(backgroundName)) {
            batch.draw(assets.getTexture(backgroundName), 0, 0,
                    GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);
        } else {
            assets.fill(batch, style.sky(), 0, 0, GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);
        }
        drawLevelTextures(batch, assets);
        if (!theme.hidesGoal()) {
            haloColor.set(style.halo());
            haloColor.a = 0.6f + 0.4f * MathUtils.sin(stateTime * 4f);
            assets.drawTinted(batch, "goal_halo", haloColor, goal.getX() - 8f, goal.getY() - 12f);
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawLevel();
        if (theme.hidesGoal()) {
            drawSpikes(goal.getX(), goal.getY(), goal.getWidth(), 20f);
        } else if (!assets.hasTexture("goal_" + themeKey)) {
            setColor(theme.getGoalColor());
            shapeRenderer.rect(goal.getX(), goal.getY(), goal.getWidth(), goal.getHeight());
        }
        shapeRenderer.end();

        batch.begin();
        if (!theme.hidesGoal() && assets.hasTexture("goal_" + themeKey)) {
            batch.draw(assets.getTexture("goal_" + themeKey), goal.getX(), goal.getY(),
                    GameConfig.TILE, GameConfig.TILE);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawTraps();
        shapeRenderer.end();

        batch.begin();
        drawTrapSprites(batch, assets);
        drawCamouflagedTraps(batch, assets);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (playerSprites.getAnimation(spriteState == null ? PlayerSprites.State.IDLE : spriteState) == null) {
            if (player.isBoosted()) shapeRenderer.setColor(1f, 0.80f, 0.25f, 1f);
            else shapeRenderer.setColor(1f, 0.44f, 0.38f, 1f);
            shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }
        shapeRenderer.end();

        batch.begin();
        Animation<TextureRegion> animation = playerSprites.getAnimation(
                spriteState == null ? PlayerSprites.State.IDLE : spriteState);
        if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(stateTime);
            float size = 64f;
            float drawX = player.getX() + player.getWidth() / 2f - size / 2f;
            float drawY = player.getY() - 12f;
            if (facingLeft) batch.draw(frame, drawX + size, drawY, -size, size);
            else batch.draw(frame, drawX, drawY, size, size);
        }
        hud.draw(batch);
        taunt.draw(batch);
        batch.end();
    }

    static String fakeGoalSprite(LevelTheme theme, boolean goalTexturePresent) {
        return theme == LevelTheme.SRI_LANKA && goalTexturePresent ? "goal_srilanka" : null;
    }

    static boolean maskHiddenSlabWithBackground(LevelTheme theme, boolean backgroundPresent) {
        return theme == LevelTheme.LAPONIE && backgroundPresent;
    }

    /** Copie le fond derrière la dalle cachée et rend le faux piment identique au vrai. */
    private void drawCamouflagedTraps(SpriteBatch batch, Assets assets) {
        String fakeSprite = fakeGoalSprite(theme, assets.hasTexture("goal_" + themeKey));
        boolean texturedMask = maskHiddenSlabWithBackground(theme, assets.hasTexture("bg_" + themeKey));
        for (Trap trap : traps) {
            if (!trap.isVisible()) continue;
            if (trap instanceof FakeChili && fakeSprite != null) {
                batch.draw(assets.getTexture(fakeSprite), trap.getX(), trap.getY(),
                        trap.getWidth(), trap.getHeight());
            } else if (trap instanceof HiddenSlab slab && !slab.isRevealed() && texturedMask) {
                Texture bg = assets.getTexture("bg_" + themeKey);
                float x = trap.getX(), y = trap.getY();
                batch.draw(bg, x, y, trap.getWidth(), trap.getHeight(),
                        x / GameConfig.VIEW_WIDTH, 1f - y / GameConfig.VIEW_HEIGHT,
                        (x + trap.getWidth()) / GameConfig.VIEW_WIDTH,
                        1f - (y + trap.getHeight()) / GameConfig.VIEW_HEIGHT);
            }
        }
    }

    static boolean isFloorSurface(Level level, int col, int row) {
        return level.isWall(col, row) && !level.isWall(col, row + 1);
    }

    private void drawLevelTextures(SpriteBatch batch, Assets assets) {
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getCols(); col++) {
                if (!level.isWall(col, row)) continue;
                String wall = "wall_" + themeKey;
                String floor = "floor_" + themeKey;
                String name = isFloorSurface(level, col, row) && assets.hasTexture(floor) ? floor : wall;
                if (assets.hasTexture(name)) {
                    batch.draw(assets.getTexture(name), col * Level.TILE, row * Level.TILE,
                            Level.TILE, Level.TILE);
                }
            }
        }
    }

    private void drawLevel() {
        Assets assets = game.getAssets();
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getCols(); col++) {
                if (level.isWall(col, row)) {
                    String floor = "floor_" + themeKey;
                    String name = isFloorSurface(level, col, row) && assets.hasTexture(floor)
                            ? floor : "wall_" + themeKey;
                    if (!assets.hasTexture(name)) {
                        setColor(theme.getWallColor());
                        shapeRenderer.rect(col * Level.TILE, row * Level.TILE, Level.TILE, Level.TILE);
                    }
                }
            }
        }
    }

    /** Pièges que TK dessine de façon spéciale : on garde entièrement son dessin. */
    private static boolean keepsShapeDrawing(Trap trap) {
        return trap instanceof FakeChili || trap instanceof HiddenSlab || trap instanceof SkyFall
                || trap instanceof TiltSlab || trap instanceof InvisibleBlock;
    }

    /** Pièges avec un décor autour (singe, vendeur, tableau) : on garde le décor, l'image va par-dessus. */
    private static boolean keepsBackdrop(Trap trap) {
        return trap instanceof Monkey || trap instanceof SimitVendor || trap instanceof Croissant;
    }

    /** Vrai si ce piège doit être dessiné avec son image .png. */
    private boolean hasTrapSprite(Trap trap) {
        return !keepsShapeDrawing(trap) && game.getAssets().hasTexture(trap.getSpriteName());
    }

    /** Dessine l'image .png de chaque piège qui en a une. */
    private void drawTrapSprites(SpriteBatch batch, Assets assets) {
        for (Trap trap : traps) {
            if (!trap.isVisible() || !hasTrapSprite(trap)) {
                continue;
            }
            if (trap instanceof Croissant croissant && croissant.isEaten()) {
                continue;
            }
            batch.draw(assets.getTexture(trap.getSpriteName()),
                    trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
        }
    }

    private void drawTraps() {
    for (Trap trap : traps) {
        if (!trap.isVisible()) {
            continue;
        }
        if (hasTrapSprite(trap) && !keepsBackdrop(trap)) {
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
             // Même apparence que G, y compris lorsqu'un sprite est disponible.
             if (fakeGoalSprite(theme, game.getAssets().hasTexture("goal_" + themeKey)) == null) {
                 setColor(theme.getGoalColor());
                 shapeRenderer.rect(trap.getX(), trap.getY(), trap.getWidth(), trap.getHeight());
             }
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
             if (!slab.isRevealed() && !maskHiddenSlabWithBackground(theme,
                     game.getAssets().hasTexture("bg_" + themeKey))) {
                 // Sans PNG, la couleur unie masque la dalle comme auparavant.
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