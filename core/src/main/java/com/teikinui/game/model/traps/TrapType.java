package com.teikinui.game.model.traps;

/** Six pièges de Tahiti + pièges de Chine. */
public enum TrapType {
    // --- Pièges de Tahiti ---
    COCONUT('c', TrapFamily.FALLING, "coconut"),
    STARFISH('s', TrapFamily.PROJECTILE, "starfish"),
    QUICKSAND('q', TrapFamily.GROUND, "quicksand"),
    RETURN_DOOR('d', TrapFamily.TELEPORT, "return_door"),
    CLOUD_SHARK('h', TrapFamily.FALLING, "cloud_shark"),
    FAKE_FLOOR('f', TrapFamily.GROUND, "fake_floor"),

    // --- Pièges de Chine ---
    SKY_FALL('k', TrapFamily.FALLING, "sky"),
    WAVE('w', TrapFamily.PROJECTILE, "wave"),
    HIDDEN_DALLE('i', TrapFamily.GROUND, "hidden_dalle"),
    FAKE_PALM('p', TrapFamily.GROUND, "fake_palm"),
    SPIKE_SPAWN('x', TrapFamily.GROUND, "spike"),
    TRAP_DALLE('u', TrapFamily.GROUND, "trap_dalle"),
    FAKE_DALLE('v', TrapFamily.GROUND, "fake_dalle");

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