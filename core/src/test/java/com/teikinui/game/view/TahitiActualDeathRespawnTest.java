package com.teikinui.game.view;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.teikinui.game.TripTrapGame;
import com.teikinui.game.app.InputHandler;
import com.teikinui.game.level.LevelManager;
import com.teikinui.game.level.LevelTheme;
import com.teikinui.game.model.CollisionSystem;
import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameEvent;
import com.teikinui.game.model.GameSession;
import com.teikinui.game.model.GameTimer;
import com.teikinui.game.model.Goal;
import com.teikinui.game.model.Level;
import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.falling.CocoFall;
import sun.misc.Unsafe;

/** Uses the real PlayScreen.update -> contacts -> death -> loadLevel pipeline, without an OpenGL window. */
class TahitiActualDeathRespawnTest {
    private static final Unsafe UNSAFE = unsafe();

    @Test
    void centralMapCoconutDeathReloadsFixedTrapsAndKeepsReturnWaveDormantUntilGoalMoves() throws Exception {
        Files originalFiles = Gdx.files;
        Input originalInput = Gdx.input;
        try {
            Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class<?>[] {Files.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("internal")) {
                            String name = (String) args[0];
                            Path path = Path.of(name);
                            if (!path.toFile().exists()) path = Path.of("..").resolve(path);
                            return new FileHandle(path.toFile());
                        }
                        return null;
                    });
            Gdx.input = (Input) Proxy.newProxyInstance(Input.class.getClassLoader(), new Class<?>[] {Input.class},
                    (proxy, method, args) -> method.getReturnType() == boolean.class ? false : null);

            Assets assets = (Assets) UNSAFE.allocateInstance(Assets.class);
            TripTrapGame game = (TripTrapGame) UNSAFE.allocateInstance(TripTrapGame.class);
            set(game, "assets", assets);
            PlayScreen screen = (PlayScreen) UNSAFE.allocateInstance(PlayScreen.class);
            set(screen, "game", game);
            set(screen, "theme", LevelTheme.TAHITI); // no GL/font/audio initialization in this test harness
            set(screen, "attempt", -1); // loadLevel() starts at attempt zero despite pre-set theme
            set(screen, "style", ThemeStyle.of(LevelTheme.TAHITI));
            set(screen, "themeKey", "tahiti");
            set(screen, "playerSprites", UNSAFE.allocateInstance(PlayerSprites.class));
            set(screen, "taunt", UNSAFE.allocateInstance(TauntBubble.class));
            set(screen, "levels", new LevelManager(LevelTheme.TAHITI));
            set(screen, "session", new GameSession());
            set(screen, "timer", new GameTimer());
            set(screen, "keyboard", new InputHandler());
            set(screen, "collisionSystem", new CollisionSystem());
            EventBus bus = new EventBus();
            set(screen, "bus", bus);
            bus.subscribe((GameEvent event) -> {
                if (event == GameEvent.PLAYER_DIED) {
                    try { set(screen, "died", true); } catch (Exception e) { throw new RuntimeException(e); }
                }
            });
            bus.subscribe((GameSession) field(screen, "session"));

            invokeLoad(screen);
            Level firstLevel = (Level) field(screen, "level");
            Player firstPlayer = (Player) field(screen, "player");
            Goal firstGoal = (Goal) field(screen, "goal");
            List<Trap> before = traps(screen);
            assertEquals(firstLevel.createTraps(firstPlayer).size(), before.size(),
                    "Tahiti ne doit plus générer trois pièges aléatoires");
            List<float[]> startPositions = before.stream().map(t -> new float[] {t.getX(), t.getY()}).toList();
            CocoFall central = before.stream().filter(CocoFall.class::isInstance)
                    .map(CocoFall.class::cast).filter(t -> t.getX() == 18 * Level.TILE && t.getY() == 10 * Level.TILE)
                    .findFirst().orElseThrow();
            firstPlayer.teleport(central.getX(), 7 * Level.TILE); // fixed floor below the central coconut
            for (int i = 0; i < 30 && field(screen, "level") == firstLevel; i++) screen.update(1f / 30f);
            assertEquals(Trap.State.DONE, central.getState(), "Central map coconut must kill via contacts");
            // No die PNG in this headless harness: update() itself calls loadLevel in the death frame.
            assertNotSame(firstLevel, field(screen, "level"));
            assertNotSame(firstPlayer, field(screen, "player"));
            assertNotSame(firstGoal, field(screen, "goal"));
            assertTrue(((Level) field(screen, "level")).getAttempt() >= 1);
            List<Trap> after = traps(screen);
            assertEquals(before.size(), after.size());
            for (int i = 0; i < before.size(); i++) {
                assertNotSame(before.get(i), after.get(i));
                assertEquals(startPositions.get(i)[0], after.get(i).getX(), "fixed/extra trap X " + i);
                assertEquals(startPositions.get(i)[1], after.get(i).getY(), "fixed/extra trap Y " + i);
            }
            assertTrue(after.stream().anyMatch(t -> t instanceof CocoFall &&
                    t.getX() == 18 * Level.TILE && t.getY() == 10 * Level.TILE && t.getState() == Trap.State.WAITING));
            List<Trap> wave = (List<Trap>) field(screen, "tahitiReturnWave");
            assertEquals(3, wave.size());
            assertTrue(after.stream().noneMatch(wave::contains));
            Level reloaded = (Level) field(screen, "level");
            screen.update(1f / 30f); // still at P: wave must not move or render before G -> P
            assertSame(reloaded, field(screen, "level"), "No second respawn may hide the premature wave");
            assertTrue(traps(screen).stream().noneMatch(wave::contains));
            assertTrue(wave.stream().allMatch(t -> t.getState() == Trap.State.WAITING));
            ((Player) field(screen, "player")).teleport(((Goal) field(screen, "goal")).getX()
                    - 2 * Level.TILE, ((Goal) field(screen, "goal")).getY());
            screen.update(1f / 30f);
            assertTrue(((Goal) field(screen, "goal")).hasMovedToStart());
            assertTrue(traps(screen).containsAll(wave));
            screen.update(1f / 30f);
            assertEquals(3, traps(screen).stream().filter(wave::contains).count());
        } finally {
            Gdx.files = originalFiles;
            Gdx.input = originalInput;
        }
    }

    private static List<Trap> traps(PlayScreen screen) throws Exception { return (List<Trap>) field(screen, "traps"); }
    private static void invokeLoad(PlayScreen screen) throws Exception {
        var method = PlayScreen.class.getDeclaredMethod("loadLevel");
        method.setAccessible(true);
        method.invoke(screen);
    }
    private static Object field(Object target, String name) throws Exception {
        Field f = findField(target.getClass(), name);
        f.setAccessible(true);
        return f.get(target);
    }
    private static void set(Object target, String name, Object value) throws Exception {
        Field f = findField(target.getClass(), name);
        f.setAccessible(true);
        f.set(target, value);
    }
    private static Field findField(Class<?> type, String name) throws Exception {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try { return current.getDeclaredField(name); } catch (NoSuchFieldException ignored) { }
        }
        throw new NoSuchFieldException(name);
    }
    private static Unsafe unsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
}
