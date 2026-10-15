package dev.explorercraft.lift;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class Lift implements ModInitializer {
    public static final String MOD_ID = "lift";

    private static final ResourceKey<CreativeModeTab> REDSTONE_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("redstone_blocks"));

    /** What the plain plate travels at, in blocks per tick — about two blocks a second. */
    public static final double BASE_SPEED = 0.1;

    /**
     * The three tiers, crafted from one another and differing only in speed. Unlike a vanilla
     * pressure plate they keep their collision box, so a rider stands on the plate itself while it
     * is parked rather than on whatever is underneath.
     */
    public static final LiftPlateBlock LIFT_PLATE = plate("lift_plate", BASE_SPEED);
    public static final LiftPlateBlock FASTER_LIFT_PLATE = plate("faster_lift_plate", 2 * BASE_SPEED);
    public static final LiftPlateBlock FASTEST_LIFT_PLATE = plate("fastest_lift_plate", 3 * BASE_SPEED);

    /**
     * One type for all three tiers: {@link LiftPlateEntity} carries the plate it came from, so the
     * block that goes back up is the block that came down.
     *
     * Has to stay serializable: {@code Entity#startRiding} refuses to seat a passenger on a
     * vehicle whose type cannot be saved, so {@code noSave()} and being rideable are mutually
     * exclusive. {@link LiftPlateEntity} therefore saves its trip and resumes it on load, which is
     * what keeps a plate from being stranded as an entity with its block missing from the world.
     * {@code noSummon} keeps it out of {@code /summon}, where it would have no trip to make.
     */
    public static final EntityType<LiftPlateEntity> LIFT_PLATE_ENTITY = EntityType.Builder
            .<LiftPlateEntity>of(LiftPlateEntity::new, MobCategory.MISC)
            .sized(0.98F, 0.0625F)
            .passengerAttachments(new Vec3(0.0, 0.0625, 0.0))
            .noSummon()
            .build(ResourceKey.create(Registries.ENTITY_TYPE, id("lift_plate")));

    @Override
    public void onInitialize() {
        register("lift_plate", LIFT_PLATE);
        register("faster_lift_plate", FASTER_LIFT_PLATE);
        register("fastest_lift_plate", FASTEST_LIFT_PLATE);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("lift_plate"), LIFT_PLATE_ENTITY);
        CreativeModeTabEvents.modifyOutputEvent(REDSTONE_TAB).register(output -> {
            output.accept(LIFT_PLATE);
            output.accept(FASTER_LIFT_PLATE);
            output.accept(FASTEST_LIFT_PLATE);
        });
    }

    private static LiftPlateBlock plate(String path, double speed) {
        return new LiftPlateBlock(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id(path)))
                .mapColor(MapColor.STONE)
                .forceSolidOn()
                .strength(0.5F)
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.STONE), speed);
    }

    /** A plate and the item that places it, which is also what the block drops and the tab shows. */
    private static void register(String path, LiftPlateBlock block) {
        Registry.register(BuiltInRegistries.BLOCK, id(path), block);
        Registry.register(BuiltInRegistries.ITEM, id(path), new BlockItem(block, new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id(path)))
                .useBlockDescriptionPrefix()));
    }

    /**
     * Called from {@link LiftPlateBlock#entityInside} every tick a player overlaps a parked plate,
     * so it has to be idempotent — a player already aboard something is left alone. Takes the
     * block out of the world and hands the trip to {@link LiftPlateEntity}, which owns it from
     * there and puts it back when the trip ends.
     */
    static void beginAscend(ServerLevel level, BlockPos platePos, LiftPlateBlock block, ServerPlayer player) {
        if (player.isPassenger()) {
            return;
        }
        int targetY = computeStopY(level, platePos);
        if (targetY <= platePos.getY()) {
            return; // nowhere to go
        }

        LiftPlateEntity plate = LIFT_PLATE_ENTITY.create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (plate == null) {
            return;
        }

        level.removeBlock(platePos, false);
        plate.beginTrip(block, platePos, targetY);
        level.addFreshEntity(plate);
        player.startRiding(plate);
    }

    /**
     * The world Y a plate at this position travels up to.
     *
     * With a solid block cardinally beside it, the plate climbs alongside that wall and stops
     * level with its top — the height a rider can step off at. Otherwise it runs up its own column
     * and stops two blocks below the first solid block overhead. With neither, it stops at the
     * world's build height rather than climbing forever.
     */
    static int computeStopY(ServerLevel level, BlockPos platePos) {
        int plateY = platePos.getY();

        Integer ceilingY = null;
        for (int y = plateY + 1; y <= level.getMaxY(); y++) {
            BlockPos above = new BlockPos(platePos.getX(), y, platePos.getZ());
            if (level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
                ceilingY = y;
                break;
            }
        }

        int target;
        if (hasAdjacentWall(level, platePos)) {
            int y = plateY + 1;
            while (y <= level.getMaxY() && hasAdjacentWall(level, new BlockPos(platePos.getX(), y, platePos.getZ()))) {
                y++;
            }
            target = y;
        } else {
            target = ceilingY != null ? ceilingY - 2 : level.getMaxY() + 1;
        }

        if (ceilingY != null) {
            target = Math.min(target, ceilingY - 2);
        }
        return Math.max(target, plateY);
    }

    private static boolean hasAdjacentWall(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = pos.relative(direction);
            if (level.getBlockState(neighbor).isFaceSturdy(level, neighbor, direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
