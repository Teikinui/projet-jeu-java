package com.teikinui.game.model;

/**
 * Patron Décorateur : enveloppe le vrai clavier et échange gauche et droite.
 * Le joueur ne sait pas qu'on lui a inversé les flèches : c'est le piège du Vietnam.
 */
public final class ReversedInput implements PlayerInput {
    private final PlayerInput keys;

    public ReversedInput(PlayerInput keys) {
        this.keys = keys;
    }

    @Override
    public boolean isLeft() {
        return keys.isRight();
    }

    @Override
    public boolean isRight() {
        return keys.isLeft();
    }

    @Override
    public boolean isJump() {
        return keys.isJump();
    }
}