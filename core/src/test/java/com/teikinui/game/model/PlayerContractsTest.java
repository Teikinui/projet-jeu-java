package com.teikinui.game.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerContractsTest {
    private static final class FakeInput implements PlayerInput {
        boolean left;
        boolean right;
        boolean jump;

        @Override public boolean isLeft() { return left; }
        @Override public boolean isRight() { return right; }
        @Override public boolean isJump() { return jump; }
    }

    @Test
    void fakeKeyboardMovesAndJumpsOnlyFromGround() {
        Player player = new Player(32f, 0f, 32f, 32f, GameConfig.RUN_SPEED);
        FakeInput input = new FakeInput();
        input.right = true;
        input.jump = true;
        player.update(0.1f, input);
        assertEquals(GameConfig.RUN_SPEED, player.getVx());
        assertEquals(GameConfig.JUMP_SPEED + GameConfig.GRAVITY * 0.1f, player.getVy(), 0.001f);
        assertFalse(player.isOnGround());

        input.left = true;
        player.update(0.1f, input);
        assertEquals(0f, player.getVx());
        assertTrue(player.getVy() < GameConfig.JUMP_SPEED);
    }

    @Test
    void hitboxFollowsCollisionMovementAndEdgesDoNotOverlap() {
        Player player = new Player(32f, 32f, 32f, 32f, GameConfig.RUN_SPEED);
        assertEquals(32f, player.getHitbox().getX());
        assertFalse(player.getHitbox().overlaps(new Hitbox(64f, 32f, 32f, 32f)));
        player.update(0.1f, false, true, false);
        Tile[][] grid = {{Tile.WALL, Tile.WALL, Tile.WALL, Tile.WALL},
                         {Tile.WALL, Tile.EMPTY, Tile.EMPTY, Tile.WALL},
                         {Tile.WALL, Tile.EMPTY, Tile.EMPTY, Tile.WALL}};
        new CollisionSystem().move(player, new Level(grid), 0.1f);
        assertEquals(player.getX(), player.getHitbox().getX());
        assertEquals(player.getY(), player.getHitbox().getY());
        assertTrue(player.collidesWith(new Collidable() {
            @Override public Hitbox getHitbox() { return new Hitbox(player.getX(), player.getY(), 32f, 32f); }
        }));
    }
}
