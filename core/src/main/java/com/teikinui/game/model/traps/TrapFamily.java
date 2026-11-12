package com.teikinui.game.model.traps;

import com.teikinui.game.model.Player;

/** The trap families (one line per family, added by lots 2 and 3). */
public enum TrapFamily implements TrapCreator {
    FALLING((type, x, y, target) -> switch (type) {
        case COCONUT -> new CocoFall(x, y, target);
        case CLOUD_SHARK -> new CloudShark(x, y, target);
        case JAR -> new Jar(x, y, target);
        case DOOR_SLAM -> new DoorSlam(x, y, target);
        default -> throw new IllegalArgumentException("Piège incompatible : " + type);
    }),
    PROJECTILE((type, x, y, target) -> {
        if (type != TrapType.STARFISH) throw new IllegalArgumentException("Piège incompatible : " + type);
        return new Starfish(x, y, target);
    }),
    GROUND((type, x, y, target) -> switch (type) {
        case QUICKSAND -> new QuickSand(x, y, target);
        case FAKE_FLOOR -> new FakeFloorTrap(x, y, target);
        case CACTUS -> new Cactus(x, y, target);
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
