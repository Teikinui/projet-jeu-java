package com.teikinui.game.model.traps;

import com.teikinui.game.model.EventBus;
import com.teikinui.game.model.GameConfig;
import com.teikinui.game.model.Player;

/**
 * Croissant dans un tableau (France) : ce n'est pas un piège mais un bonus.
 * Quand le joueur le touche, il le mange et reçoit un boost de quelques
 * secondes : il court plus vite et saute plus haut et plus loin.
 */
public final class Croissant extends Trap {
    public static final float BOOST_TIME = 4f;
    private static final float WIDTH = 22f;
    private static final float HEIGHT = 16f;

    private boolean eaten;

    public Croissant(float x, float y, Player target) {
        super(x + (GameConfig.TILE - WIDTH) / 2f, y + (GameConfig.TILE - HEIGHT) / 2f,
                WIDTH, HEIGHT, target);
        setMarked(false);
    }

    /** Le tableau reste accroché ; seul le croissant disparaît une fois mangé. */
    public boolean isEaten() {
        return eaten;
    }

    @Override
    protected boolean isTriggered() {
        return collidesWith(target);
    }

    @Override
    protected void activate(float dt) {
        if (!eaten) {
            eaten = true;
            target.boost(BOOST_TIME);
        }
    }

    /** Le croissant ne tue jamais. */
    @Override
    public void onPlayerContact(Player player, EventBus bus) {
    }
}