package com.teikinui.game.model.traps;

import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Singe macaque (Sri Lanka) : accroché sous le plafond, il lance un fruit
 * droit sur le joueur quand celui-ci passe en dessous, puis recommence.
 * Le piège (sa hitbox) est le fruit : c'est lui qui tue.
 */
public final class Monkey extends Trap {
    private static final float FRUIT = 14f;
    private static final float RANGE = 5f * GameConfig.TILE;
    private static final float SPEED = 7f * GameConfig.TILE;
    private static final float COOLDOWN = 1.2f;

    private final float homeX;
    private final float homeY;
    private float vx;
    private float vy;
    private float cooldown;
    private boolean flying;

    public Monkey(float x, float y, Player target) {
        super(x + (GameConfig.TILE - FRUIT) / 2f, y, FRUIT, FRUIT, target);
        homeX = x;
        homeY = y;
        setMarked(false);
    }

    public float getHomeX() {
        return homeX;
    }

    public float getHomeY() {
        return homeY;
    }

    private boolean playerInRange() {
        float playerCenter = target.getX() + target.getWidth() / 2f;
        float monkeyCenter = homeX + GameConfig.TILE / 2f;
        return Math.abs(playerCenter - monkeyCenter) < RANGE && target.getY() < homeY;
    }

    @Override
    protected boolean isTriggered() {
        return playerInRange();
    }

    @Override
    protected void activate(float dt) {
        if (flying) {
            x += vx * dt;
            y += vy * dt;
            if (hitSomething()) {
                backToHand();
            }
            return;
        }
        cooldown -= dt;
        if (cooldown <= 0f && playerInRange()) {
            throwFruit();
        }
    }

    /** Vise le centre du joueur au moment du lancer. */
    private void throwFruit() {
        float dx = target.getX() + target.getWidth() / 2f - (x + FRUIT / 2f);
        float dy = target.getY() + target.getHeight() / 2f - (y + FRUIT / 2f);
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        vx = dx / length * SPEED;
        vy = dy / length * SPEED;
        flying = true;
        cooldown = COOLDOWN;
    }

    private boolean hitSomething() {
        int col = (int) Math.floor((x + FRUIT / 2f) / GameConfig.TILE);
        int row = (int) Math.floor((y + FRUIT / 2f) / GameConfig.TILE);
        return y < -GameConfig.TILE || level.isWall(col, row);
    }

    private void backToHand() {
        flying = false;
        x = homeX + (GameConfig.TILE - FRUIT) / 2f;
        y = homeY;
    }
}