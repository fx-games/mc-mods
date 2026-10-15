package dev.explorercraft.lift;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

/**
 * The plate while it is travelling. A block can only sit on the integer grid, so the plate leaves
 * the world for the length of a trip and rides as this instead: an entity moves in fractional
 * steps and the client tweens between the positions it is sent, which is what makes the travel
 * smooth. The rider is a passenger, so they are carried by the same interpolation rather than by
 * anything writing their position.
 *
 * It extends {@link FallingBlockEntity} purely to inherit a renderer — vanilla's draws whatever
 * {@link #getBlockState()} returns, so overriding that is the whole of this mod's rendering. None
 * of the falling behaviour survives: {@link #tick()} replaces it outright.
 */
public class LiftPlateEntity extends FallingBlockEntity {
    /**
     * The client is only told where the plate is every few ticks, so without this it would jump
     * from one reported position to the next. Vanilla's handler spreads each update over the ticks
     * in between, the same way boats are kept smooth; {@link #tick()} drives it on the client.
     */
    private final InterpolationHandler interpolation = new InterpolationHandler(this);

    /** Which plate is on this trip: what gets drawn, how fast it travels, and what goes back down. */
    private LiftPlateBlock plate = Lift.LIFT_PLATE;

    private BlockPos origin;
    private int targetY;
    private boolean goingHome;
    /** Set once the plate is back in the world, so teardown does not put down a second one. */
    private boolean restored;

    public LiftPlateEntity(EntityType<? extends FallingBlockEntity> type, Level level) {
        super(type, level);
    }

    /**
     * Hands the entity the trip it was spawned for. Origin is where the block came from — copied,
     * because it arrives from {@code entityInside} as the cursor vanilla re-points at every block
     * it scans an entity against. Keeping that reference makes home follow the player around, so
     * the plate comes back wherever they last stood instead of where it started.
     */
    void beginTrip(LiftPlateBlock plate, BlockPos origin, int targetY) {
        this.plate = plate;
        this.origin = origin.immutable();
        this.targetY = targetY;
        moveTo(origin.getY());
    }

    /**
     * The one thing that moves the plate. A lift runs in a shaft, so the column it was placed in is
     * the only column it is ever in: height is the only thing a trip changes, and X and Z are read
     * back off the origin every tick rather than carried forward, so nothing can nudge it sideways.
     */
    private void moveTo(double y) {
        setPos(origin.getX() + 0.5, y, origin.getZ() + 0.5);
    }

    /** Vanilla's falling-block renderer draws this, which is all the rendering the plate needs. */
    @Override
    public BlockState getBlockState() {
        return plate.defaultBlockState();
    }

    /**
     * The tier has to reach the client or every plate would be drawn as the plain one. Vanilla
     * already puts {@link #getBlockState()} into the spawn packet for the falling-block renderer,
     * so reading it back out of that packet is the whole of the sync — no extra tracked data.
     */
    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        plate = asPlate(Block.stateById(packet.getData()).getBlock());
    }

    /** A block that is not a plate — an unknown id, a corrupt save — falls back to the plain one. */
    private static LiftPlateBlock asPlate(Block block) {
        return block instanceof LiftPlateBlock liftPlate ? liftPlate : Lift.LIFT_PLATE;
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return interpolation;
    }

    /**
     * Makes the plate something you can stand on rather than fall through, which is what a rider
     * who dismounts in mid-air lands on. Vanilla's {@code canCollideWith} already defers to this,
     * so this one override is the whole of it.
     */
    @Override
    public boolean canBeCollidedWith(Entity entity) {
        return true;
    }

    @Override
    public void tick() {
        if (!(level() instanceof ServerLevel serverLevel) || origin == null) {
            interpolation.interpolate();
            return;
        }

        // Dismounting is how you get off, so an empty platform is a platform that should go home.
        if (getPassengers().isEmpty()) {
            goingHome = true;
        }

        if (goingHome) {
            double next = Math.max(getY() - plate.speed, origin.getY());
            moveTo(next);
            if (next <= origin.getY() && standingRoomIsClear(serverLevel)) {
                restoreAt(serverLevel, origin);
                discard();
            }
            return;
        }

        double next = Math.min(getY() + plate.speed, targetY);
        moveTo(next);
        // At the top it simply hovers, still carrying the rider, until they step off. Putting the
        // block back here instead would strand it at the top with no way home.
    }

    /**
     * Whether the plate can turn back into a block without doing it underneath somebody. A rider
     * who came down with the plate is standing on it the moment it lands, and a block placed under
     * their feet is a block they are stepping on — which starts the whole trip over again, forever.
     * So the plate waits, holding them up as an entity, until they walk off.
     *
     * The room checked is the plate's own footprint from its top face up two blocks: the space a
     * person standing on it takes up. A thin slab just above the plate is not enough, because a
     * plate outruns the rider on the way down — the faster tiers descend quicker than gravity
     * pulls — so at the moment it lands they are still a way above it, and a slab would read as
     * empty and drop the block right back under the feet that are about to arrive on it.
     */
    private boolean standingRoomIsClear(ServerLevel level) {
        AABB standingRoom = getBoundingBox().move(0.0, 0.0625, 0.0).expandTowards(0.0, 2.0, 0.0);
        return level.getEntities(this, standingRoom, entity -> !entity.isRemoved()).isEmpty();
    }

    /**
     * Puts the plate back into the world. Also the safety net for a trip that ends some other way,
     * so that no path can leave the block missing from the world with only an entity to show for
     * it. Deliberately does not discard: {@link #remove} calls this too, and discarding from
     * inside a removal would re-enter it.
     */
    private void restoreAt(ServerLevel level, BlockPos pos) {
        if (restored) {
            return;
        }
        restored = true;

        BlockState existing = level.getBlockState(pos);
        if (existing.isAir() || existing.canBeReplaced()) {
            level.setBlockAndUpdate(pos, plate.defaultBlockState());
        } else {
            // Somewhere to put it beats losing it.
            spawnAtLocation(level, new ItemStack(plate));
        }
        ejectPassengers();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!restored && origin != null && level() instanceof ServerLevel serverLevel) {
            restoreAt(serverLevel, origin);
        }
        super.remove(reason);
    }

    /**
     * The trip has to survive a save, because the entity has to be saveable at all — see the type
     * built in {@link Lift}. Without this a plate reloaded mid-trip would wake up with no origin
     * to return to, and sit there as an entity while its block stayed missing from the world.
     */
    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("LiftPlate", BuiltInRegistries.BLOCK.byNameCodec(), plate);
        if (origin != null) {
            output.putInt("LiftOriginX", origin.getX());
            output.putInt("LiftOriginY", origin.getY());
            output.putInt("LiftOriginZ", origin.getZ());
            output.putInt("LiftTargetY", targetY);
            output.putBoolean("LiftGoingHome", goingHome);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        plate = asPlate(input.read("LiftPlate", BuiltInRegistries.BLOCK.byNameCodec()).orElse(Lift.LIFT_PLATE));
        input.getInt("LiftOriginY").ifPresent(y -> {
            origin = new BlockPos(input.getIntOr("LiftOriginX", 0), y, input.getIntOr("LiftOriginZ", 0));
            targetY = input.getIntOr("LiftTargetY", y);
            // Whoever was aboard is long gone by the time a trip is reloaded, so it heads home.
            goingHome = true;
        });
    }
}
