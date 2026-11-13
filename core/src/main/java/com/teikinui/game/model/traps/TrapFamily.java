package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;
import com.teikinui.game.model.traps.falling.CloudShark;
import com.teikinui.game.model.traps.falling.CocoFall;
import com.teikinui.game.model.traps.ground.FakeFloorTrap;
import com.teikinui.game.model.traps.ground.QuickSand;
import com.teikinui.game.model.traps.ground.ReturnDoor;
import com.teikinui.game.model.traps.projectile.Starfish;

/** The trap families (one line per family, added by lots 2 and 3). */
public enum TrapFamily implements TrapCreator {
    FALLING((type, x, y, target) -> switch (type) {
        case COCONUT -> new CocoFall(x, y, target);
        case CLOUD_SHARK -> new CloudShark(x, y, target);
        case JAR -> new Jar(x, y, target);
        case DOOR_SLAM -> new DoorSlam(x, y, target);
        case CRUMBLE_BLOCK -> new CrumbleBlock(x, y, target);
        case DUNE -> new Dune(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    PROJECTILE((type, x, y, target) -> switch (type) {
        case STARFISH -> new Starfish(x, y, target);
        case MONKEY -> new Monkey(x, y, target);
        case SIMIT_VENDOR -> new SimitVendor(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    GROUND((type, x, y, target) -> switch (type) {
        case QUICKSAND -> new QuickSand(x, y, target);
        case FAKE_FLOOR -> new FakeFloorTrap(x, y, target);
        case CACTUS -> new Cactus(x, y, target);
        case SPIKE_FLOOR -> new SpikeFloor(x, y, target);
        case POP_SPIKE -> new PopSpike(x, y, target);
        case GROWING_SPIKE -> new GrowingSpike(x, y, target);
        case FAKE_CHILI -> new FakeChili(x, y, target);
        case INVISIBLE_BLOCK -> new InvisibleBlock(x, y, target);
        case DERVISH -> new Dervish(x, y, target);
        case LOUKOUM -> new Loukoum(x, y, target);
        case SKY_FALL -> new SkyFall(x, y, target);
        case CHARGING_WALL -> new ChargingWall(x, y, target);
        case HIDDEN_SLAB -> new HiddenSlab(x, y, target);
        case FAKE_SLAB -> new FakeSlab(x, y, target);
        case TILT_SLAB -> new TiltSlab(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    TELEPORT((type, x, y, target) -> {
        if (type != TrapType.RETURN_DOOR) throw new IllegalArgumentException("Piège incompatible : " + type);
        return new ReturnDoor(x, y, target);
    }),
    /** Objets qui aident le joueur au lieu de le piéger (France). */
    BONUS((type, x, y, target) -> {
        if (type != TrapType.CROISSANT) throw new IllegalArgumentException("Piège incompatible : " + type);
        return new Croissant(x, y, target);
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