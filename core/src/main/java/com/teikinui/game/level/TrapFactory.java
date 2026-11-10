package com.teikinui.game.level;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.Trap;
import com.teikinui.game.model.traps.TrapType;

/** Convertit une lettre de carte en piège de Tahiti. */
public final class TrapFactory {
    private TrapFactory() {
    }

    public static boolean isTrap(char letter) {
        for (TrapType type : TrapType.values()) {
            if (type.getLetter() == letter) return true;
        }
        return false;
    }

    public static Trap create(char letter, float x, float y, Player target) {
        for (TrapType type : TrapType.values()) {
            if (type.getLetter() == letter) return type.getFamily().create(type, x, y, target);
        }
        throw new IllegalArgumentException("Piège inconnu : " + letter);
    }
}
