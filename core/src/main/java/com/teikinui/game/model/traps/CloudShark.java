package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;

/** Requin qui sort du nuage : même chute de proximité que la noix de coco. */
public final class CloudShark extends FallingTrap {
    public CloudShark(float x, float y, Player target) {
        super(x, y, target);
    }
}
