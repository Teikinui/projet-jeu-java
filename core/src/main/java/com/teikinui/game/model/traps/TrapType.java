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
    CRUMBLE_BLOCK('t', TrapFamily.FALLING, "crumble_block"),
    FAKE_CHILI('v', TrapFamily.GROUND, "fake_chili"),
    INVISIBLE_BLOCK('u', TrapFamily.GROUND, "invisible_block"),
    DUNE('w', TrapFamily.FALLING, "dune"),
    MONKEY('z', TrapFamily.PROJECTILE, "monkey"),
    CROISSANT('g', TrapFamily.BONUS, "croissant"),
    DERVISH('D', TrapFamily.GROUND, "dervish"),
    LOUKOUM('L', TrapFamily.GROUND, "loukoum"),
    SIMIT_VENDOR('S', TrapFamily.PROJECTILE, "simit_vendor"),
    SKY_FALL('a', TrapFamily.GROUND, "sky_fall"),
    CHARGING_WALL('m', TrapFamily.GROUND, "charging_wall"),
    HIDDEN_SLAB('n', TrapFamily.GROUND, "hidden_slab"),
    FAKE_SLAB('x', TrapFamily.GROUND, "fake_slab"),
    TILT_SLAB('b', TrapFamily.GROUND, "tilt_slab"),
    ICICLE('I', TrapFamily.FALLING, "icicle"),
    SNOWBALL('B', TrapFamily.GROUND, "snowball");

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