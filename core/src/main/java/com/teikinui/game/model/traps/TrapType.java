package com.teikinui.game.model.traps;

/** Six pièges de Tahiti. Les lettres sont réservées dans la carte texte. */
public enum TrapType {
    COCONUT('c', TrapFamily.FALLING, "coconut"),
    STARFISH('s', TrapFamily.PROJECTILE, "starfish"),
    QUICKSAND('q', TrapFamily.GROUND, "quicksand"),
    RETURN_DOOR('d', TrapFamily.TELEPORT, "return_door"),
    CLOUD_SHARK('h', TrapFamily.FALLING, "cloud_shark"),
    FAKE_FLOOR('f', TrapFamily.GROUND, "fake_floor"),
    JAR('j', TrapFamily.FALLING, "jar"),
    DOOR_SLAM('o', TrapFamily.FALLING, "door_slam"),
    CACTUS('k', TrapFamily.GROUND, "cactus"),
    SPIKE_FLOOR('e', TrapFamily.GROUND, "spike_floor"),
    POP_SPIKE('i', TrapFamily.GROUND, "pop_spike"),
    GROWING_SPIKE('y', TrapFamily.GROUND, "growing_spike"),
    CRUMBLE_BLOCK('t', TrapFamily.FALLING, "crumble_block");

    private final char letter;
    private final TrapFamily family;
    private final String spriteName;

    TrapType(char letter, TrapFamily family, String spriteName) {
        this.letter = letter;
        this.family = family;
        this.spriteName = spriteName;
    }

    public char getLetter() {
        return letter;
    }

    public TrapFamily getFamily() {
        return family;
    }

    public String getSpriteName() {
        return spriteName;
    }
}