package com.teikinui.game.model.traps;

/** The 21 traps (one line per trap, added by lots 2 and 3). */
public enum TrapType {
    ;

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
