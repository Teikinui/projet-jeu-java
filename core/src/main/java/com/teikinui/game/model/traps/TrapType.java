package com.teikinui.game.model.traps;

import com.teikinui.game.model.ContactBounds;

/** Six pièges de Tahiti. Les lettres sont réservées dans la carte texte. */
public enum TrapType {
    COCONUT('c', TrapFamily.FALLING, "coconut", ContactBounds.fromAlphaBounds(32, 32, 5, 0, 28, 30)),
    STARFISH('s', TrapFamily.PROJECTILE, "starfish", ContactBounds.fromAlphaBounds(32, 32, 1, 2, 31, 30)),
    QUICKSAND('q', TrapFamily.GROUND, "quicksand"),
    RETURN_DOOR('d', TrapFamily.TELEPORT, "return_door", ContactBounds.fromAlphaBounds(32, 32, 3, 1, 30, 32)),
    CLOUD_SHARK('h', TrapFamily.FALLING, "cloud_shark", ContactBounds.fromAlphaBounds(32, 32, 1, 0, 32, 32)),
    FAKE_FLOOR('f', TrapFamily.GROUND, "fake_floor"),
    JAR('j', TrapFamily.FALLING, "jar", ContactBounds.fromAlphaBounds(32, 32, 5, 0, 28, 32)),
    DOOR_SLAM('o', TrapFamily.FALLING, "door_slam"),
    CACTUS('k', TrapFamily.GROUND, "cactus", ContactBounds.fromAlphaBounds(32, 32, 2, 0, 31, 32)),
    SPIKE_FLOOR('e', TrapFamily.GROUND, "spike_floor", ContactBounds.fromAlphaBounds(32, 20, 0, 1, 32, 20)),
    POP_SPIKE('i', TrapFamily.GROUND, "pop_spike"),
    GROWING_SPIKE('y', TrapFamily.GROUND, "growing_spike", ContactBounds.fromAlphaBounds(16, 32, 1, 0, 15, 32)),
    CRUMBLE_BLOCK('t', TrapFamily.FALLING, "crumble_block"),
    FAKE_CHILI('v', TrapFamily.GROUND, "fake_chili", ContactBounds.fromAlphaBounds(32, 32, 4, 0, 27, 30)),
    INVISIBLE_BLOCK('u', TrapFamily.GROUND, "invisible_block"),
    DUNE('w', TrapFamily.FALLING, "dune", ContactBounds.fromAlphaBounds(32, 32, 0, 4, 32, 32)),
    MONKEY('z', TrapFamily.PROJECTILE, "monkey"),
    CROISSANT('g', TrapFamily.BONUS, "croissant", ContactBounds.fromAlphaBounds(22, 16, 0, 1, 22, 16)),
    DERVISH('D', TrapFamily.GROUND, "dervish", ContactBounds.fromAlphaBounds(24, 30, 1, 0, 23, 30)),
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
    private final ContactBounds contactBounds;

    TrapType(char letter, TrapFamily family, String spriteName) {
        this(letter, family, spriteName, ContactBounds.FULL);
    }

    TrapType(char letter, TrapFamily family, String spriteName, ContactBounds contactBounds) {
        this.letter = letter;
        this.family = family;
        this.spriteName = spriteName;
        this.contactBounds = contactBounds;
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

    public ContactBounds getContactBounds() {
        return contactBounds;
    }
}