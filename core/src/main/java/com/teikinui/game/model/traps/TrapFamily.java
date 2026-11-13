package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.falling.CloudShark;
import com.teikinui.game.model.traps.falling.CocoFall;
import com.teikinui.game.model.traps.falling.SkyFall;
import com.teikinui.game.model.traps.ground.FakeDalle;
import com.teikinui.game.model.traps.ground.FakeFloorTrap;
import com.teikinui.game.model.traps.ground.FakePalm;
import com.teikinui.game.model.traps.ground.HiddenDalle;
import com.teikinui.game.model.traps.ground.QuickSand;
import com.teikinui.game.model.traps.ground.ReturnDoor;
import com.teikinui.game.model.traps.ground.SpikeSpawn;
import com.teikinui.game.model.traps.ground.TrapDalle;
import com.teikinui.game.model.traps.projectile.Starfish;
import com.teikinui.game.model.traps.projectile.Wave;

/** The trap families (one line per family, added by lots 2 and 3). */
public enum TrapFamily implements TrapCreator {
    FALLING((type, x, y, target) -> switch (type) {
        case COCONUT -> new CocoFall(x, y, target);
        case SKY_FALL -> new SkyFall(x, y, target);
        case CLOUD_SHARK -> new CloudShark(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    PROJECTILE((type, x, y, target) -> switch (type) {
        case STARFISH -> new Starfish(x, y, target);
        case WAVE -> new Wave(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    GROUND((type, x, y, target) -> switch (type) {
        case QUICKSAND -> new QuickSand(x, y, target);
        case FAKE_FLOOR -> new FakeFloorTrap(x, y, target);
        case HIDDEN_DALLE -> new HiddenDalle(x, y, target);
        case FAKE_PALM -> new FakePalm(x, y, target);
        case SPIKE_SPAWN -> new SpikeSpawn(x, y, target);
        case TRAP_DALLE -> new TrapDalle(x, y, target);
        case FAKE_DALLE -> new FakeDalle(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    TELEPORT((type, x, y, target) -> {
        if (type != TrapType.RETURN_DOOR) throw new IllegalArgumentException("Piège incompatible : " + type);
        return new ReturnDoor(x, y, target);
    });

    private final TrapCreator creator;

    TrapFamily(TrapCreator creator) {
        this.creator = creator;
    }

    @Override
    public Trap create(TrapType type, float x, float y, Player target) {
        return creator.create(type, x, y, target);
    }
}