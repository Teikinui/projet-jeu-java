package com.teikinui.game.model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.teikinui.game.level.LevelLoader;
import com.teikinui.game.level.TrapFactory;
import com.teikinui.game.model.traps.Trap;

class TahitiTrapsTest {
    private Player player() { return new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED); }

    @Test void coconutFallsWhenPlayerApproachesAndKillsOnTouch() {
        Player player = player();
        Trap trap = TrapFactory.create('c', 32f, 128f, player);
        EventBus bus = new EventBus();
        GameSession session = new GameSession();
        bus.subscribe(session);
        trap.update(0.25f);
        assertEquals(Trap.State.TRIGGERED, trap.getState());
        assertTrue(trap.getY() < 128f);
        trap.update(0.5f);
        trap.onPlayerContact(player, bus);
        assertEquals(1, session.getDeathCount());
        assertEquals(Trap.State.DONE, trap.getState());
        trap.onPlayerContact(player, bus);
        assertEquals(1, session.getDeathCount());
    }

    @Test void starfishLaunchesHorizontallyAtPlayerAndKillsOnContact() {
        Player player = new Player(128f, 32f, 32f, 32f, 200f);
        Trap starfish = TrapFactory.create('s', 32f, 32f, player);
        starfish.update(0.25f);
        assertTrue(starfish.getX() > 32f);
        GameSession session = new GameSession();
        EventBus bus = new EventBus();
        bus.subscribe(session);
        starfish.update(0.5f);
        starfish.onPlayerContact(player, bus);
        assertEquals(1, session.getDeathCount());
    }

    @Test void quicksandSlowsPlayerOnlyWhileTouching() {
        Player player = player();
        Trap sand = TrapFactory.create('q', 32f, 32f, player);
        EventBus bus = new EventBus();
        sand.update(0.1f);
        sand.onPlayerContact(player, bus);
        player.update(0f, false, true, false);
        assertTrue(player.getVx() > 0 && player.getVx() < GameConfig.RUN_SPEED);
        player.update(0f, false, true, false);
        assertEquals(GameConfig.RUN_SPEED, player.getVx());
    }

    @Test void doorReturnsPlayerToMapStart() {
        Player player = player();
        Level level = LevelLoader.fromString("#####\n#P.d#\n#####");
        player.teleport(3 * Level.TILE, Level.TILE);
        Trap door = TrapFactory.create('d', 3 * Level.TILE, Level.TILE, player);
        door.setStart(level.getStartX(), level.getStartY());
        door.onPlayerContact(player, new EventBus());
        assertEquals(Level.TILE, player.getX());
        assertEquals(Level.TILE, player.getY());
        assertEquals(Trap.State.DONE, door.getState());
    }

    @Test void sharkEmergesFromCloudWhenPlayerApproachesAndKillsOnContact() {
        Player player = player();
        Trap shark = TrapFactory.create('h', 32f, 128f, player);
        shark.update(0.25f);
        assertTrue(shark.getY() < 128f);
        GameSession session = new GameSession();
        EventBus bus = new EventBus();
        bus.subscribe(session);
        shark.update(0.5f);
        shark.onPlayerContact(player, bus);
        assertEquals(1, session.getDeathCount());
    }

    @Test void fakeFloorTrapDoesNotHarmPlayer() {
        Player player = player();
        Trap fake = TrapFactory.create('f', 32f, 32f, player);
        GameSession session = new GameSession();
        EventBus bus = new EventBus();
        bus.subscribe(session);
        fake.onPlayerContact(player, bus);
        assertEquals(0, session.getDeathCount());
        assertEquals(Trap.State.DONE, fake.getState());
    }

    @Test void factoryAndLevelLoaderPreserveSixTrapLettersAndSpawnThem() {
        for (char letter : "csqdhf".toCharArray()) assertTrue(TrapFactory.isTrap(letter));
        assertFalse(TrapFactory.isTrap('.'));
        assertThrows(IllegalArgumentException.class, () -> TrapFactory.create('?', 0, 0, player()));
        Level level = LevelLoader.fromString("#########\n#Pcsqdhf#\n#########");
        List<Trap> traps = level.createTraps(player());
        assertEquals(6, traps.size());
        assertEquals(2 * Level.TILE, traps.get(0).getX());
    }

    @Test void busNotifiesEverySubscriber() {
        EventBus bus = new EventBus();
        List<GameEvent> received = new ArrayList<>();
        bus.subscribe(received::add);
        bus.subscribe(received::add);
        bus.publish(GameEvent.PLAYER_DIED);
        assertEquals(List.of(GameEvent.PLAYER_DIED, GameEvent.PLAYER_DIED), received);
    }
}
