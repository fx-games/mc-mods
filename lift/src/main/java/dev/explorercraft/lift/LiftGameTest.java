package dev.explorercraft.lift;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/// {@link Lift#computeStopY} is the stop-height math, checked directly against a real level. The
/// last test is the one that matters most: it puts a rider on a plate and watches the block itself
/// travel, because two bugs got through the math tests alone — a trigger that never fired, and a
/// version that moved the player while leaving the plate sitting on the ground.
public class LiftGameTest {
    @GameTest
    public void stopsTwoBlocksBelowACeiling(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos plate = helper.absolutePos(new BlockPos(1, 1, 1));
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.STONE);

        int stopY = Lift.computeStopY(level, plate);

        if (stopY != plate.getY() + 1) {
            throw helper.assertionException("expected to stop 2 below the ceiling (y=%d), got y=%d"
                    .formatted(plate.getY() + 1, stopY));
        }

        helper.succeed();
    }

    @GameTest
    public void risesToTheHeightOfAnAdjacentSolidBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos plate = helper.absolutePos(new BlockPos(1, 1, 1));
        helper.setBlock(new BlockPos(2, 1, 1), Blocks.STONE);

        int stopY = Lift.computeStopY(level, plate);

        if (stopY != plate.getY() + 1) {
            throw helper.assertionException("expected to rise to the adjacent block's height (y=%d), got y=%d"
                    .formatted(plate.getY() + 1, stopY));
        }

        helper.succeed();
    }

    /// The elevator case: a wall three blocks tall beside the plate should carry it all the way to
    /// the wall's top, not one block up. Stopping short would strand the rider inside the shaft.
    @GameTest
    public void climbsAlongsideATallWallToItsTop(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos plate = helper.absolutePos(new BlockPos(1, 1, 1));
        helper.setBlock(new BlockPos(2, 1, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 2, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 3, 1), Blocks.STONE);

        int stopY = Lift.computeStopY(level, plate);

        if (stopY != plate.getY() + 3) {
            throw helper.assertionException("expected to climb to the top of the 3-tall wall (y=%d), got y=%d"
                    .formatted(plate.getY() + 3, stopY));
        }

        helper.succeed();
    }

    @GameTest
    public void ceilingClampsAWallThatRunsTooHigh(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos plate = helper.absolutePos(new BlockPos(1, 1, 1));
        helper.setBlock(new BlockPos(2, 1, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 2, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 3, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.STONE);

        int stopY = Lift.computeStopY(level, plate);

        if (stopY != plate.getY() + 1) {
            throw helper.assertionException("the ceiling should win over the taller wall (y=%d), got y=%d"
                    .formatted(plate.getY() + 1, stopY));
        }

        helper.succeed();
    }

    /// A GameTest structure is itself bounded, so an "open" shaft here still hits a ceiling
    /// eventually — just the test area's, not a placed one. That's enough to exercise the fallback
    /// branch: it only checks the scan terminates within the world's build height instead of
    /// looping forever, since a real unbounded shaft needs a taller world than a test structure.
    @GameTest
    public void openShaftStaysWithinTheWorldsBuildHeight(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos plate = helper.absolutePos(new BlockPos(1, 1, 1));

        int stopY = Lift.computeStopY(level, plate);

        if (stopY <= plate.getY() || stopY > level.getMaxY() + 1) {
            throw helper.assertionException("expected a bounded stop above y=%d and at most y=%d, got y=%d"
                    .formatted(plate.getY(), level.getMaxY() + 1, stopY));
        }

        helper.succeed();
    }

    /**
     * The trip as a player really experiences it: a rider who is subject to the trigger every tick
     * rather than once, who stands on whatever is under them, and who is not teleported anywhere by
     * the test. Both bugs that reached the user hid in exactly that gap.
     */
    @GameTest(maxTicks = 400)
    public void aRealisticRideEndsWithThePlateBackWhereItStarted(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(1, 2, 1), Lift.LIFT_PLATE);
        helper.setBlock(new BlockPos(2, 2, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 3, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(0, 1, 1), Blocks.STONE); // somewhere to step off onto

        BlockPos origin = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos aside = helper.absolutePos(new BlockPos(0, 2, 1));
        ServerPlayer rider = mockPlayer(helper, GameType.CREATIVE);
        rider.setPos(origin.getX() + 0.5, origin.getY() + 0.0625, origin.getZ() + 0.5);
        // Vanilla scans the blocks an entity is inside with one cursor it keeps re-pointing, so
        // that is what entityInside is really handed. Anything that stores the position it is
        // given without copying it goes home to wherever the player last stood.
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        helper.startSequence()
                .thenExecuteFor(60, () -> { simulateTick(level, rider, cursor); assertStaysInItsColumn(helper, origin); })
                .thenExecute(() -> {
                    if (!(rider.getVehicle() instanceof LiftPlateEntity plate)) {
                        throw helper.assertionException("stepping on should have put the rider aboard the plate");
                    }
                    if (plate.getY() < origin.getY() + 2 - 0.001) {
                        throw helper.assertionException("expected a climb to the top of the wall, y=%.2f (from %d)"
                                .formatted(plate.getY(), origin.getY()));
                    }
                    rider.stopRiding(); // what pressing shift does
                })
                .thenExecuteFor(60, () -> { simulateTick(level, rider, cursor); assertStaysInItsColumn(helper, origin); })
                .thenExecute(() -> {
                    if (rider.getY() < origin.getY()) {
                        throw helper.assertionException("the rider fell through the plate to y=%.2f".formatted(rider.getY()));
                    }
                    if (level.getBlockState(origin).getBlock() == Lift.LIFT_PLATE) {
                        throw helper.assertionException("the plate turned back into a block under the rider, "
                                + "which is what re-launches it forever");
                    }
                    rider.setPos(aside.getX() + 0.5, aside.getY(), aside.getZ() + 0.5); // step off
                })
                .thenExecuteFor(20, () -> { simulateTick(level, rider, cursor); assertStaysInItsColumn(helper, origin); })
                .thenExecute(() -> {
                    if (!findPlates(helper).equals(List.of(origin))) {
                        throw helper.assertionException("expected the one plate back at %s, found %s"
                                .formatted(origin, findPlates(helper)));
                    }
                    if (!helper.getEntities(Lift.LIFT_PLATE_ENTITY).isEmpty()) {
                        throw helper.assertionException("the travelling plate should be gone once the block is back");
                    }
                })
                .thenSucceed();
    }

    /// Two and three times the base plate's speed, measured the only way that matters: how long the
    /// same two-block climb takes. The speed has to come off the plate that started the trip, so a
    /// plate that ignores its own tier — or an entity that keeps a constant — shows up here.
    @GameTest(maxTicks = 200)
    public void thePlainPlateClimbsTwoBlocksInTwentyTicks(GameTestHelper helper) {
        timeAClimb(helper, Lift.LIFT_PLATE, 20);
    }

    @GameTest(maxTicks = 200)
    public void aFasterPlateClimbsTwiceAsQuickly(GameTestHelper helper) {
        timeAClimb(helper, Lift.FASTER_LIFT_PLATE, 10);
    }

    @GameTest(maxTicks = 200)
    public void aFastestPlateClimbsThreeTimesAsQuickly(GameTestHelper helper) {
        timeAClimb(helper, Lift.FASTEST_LIFT_PLATE, 7); // two blocks at 0.3 lands on the 7th tick
    }

    /// Rides the given plate up a two-block wall and checks both ends of the trip: the tick it
    /// arrives on, and that the block that comes home is the same tier that left.
    private static void timeAClimb(GameTestHelper helper, LiftPlateBlock plate, int expectedTicks) {
        ServerLevel level = helper.getLevel();
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(1, 2, 1), plate);
        helper.setBlock(new BlockPos(2, 2, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(2, 3, 1), Blocks.STONE);
        helper.setBlock(new BlockPos(0, 1, 1), Blocks.STONE); // somewhere to step off onto

        BlockPos origin = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos aside = helper.absolutePos(new BlockPos(0, 2, 1));
        ServerPlayer rider = mockPlayer(helper, GameType.CREATIVE);
        rider.setPos(origin.getX() + 0.5, origin.getY() + 0.0625, origin.getZ() + 0.5);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        int[] ticks = {0};
        int[] arrivedOn = {0};
        helper.startSequence()
                .thenExecuteFor(60, () -> {
                    simulateTick(level, rider, cursor);
                    ticks[0]++;
                    if (arrivedOn[0] == 0 && rider.getVehicle() instanceof LiftPlateEntity riding
                            && riding.getY() >= origin.getY() + 2 - 1.0E-6) {
                        arrivedOn[0] = ticks[0];
                    }
                })
                .thenExecute(() -> {
                    // The first tick is the step-on that starts the trip, and the plate's own first
                    // move lands a tick either side of it depending on entity order.
                    int travelled = arrivedOn[0] - 1;
                    if (arrivedOn[0] == 0 || Math.abs(travelled - expectedTicks) > 1) {
                        throw helper.assertionException("expected the climb to take about %d ticks, took %s"
                                .formatted(expectedTicks, arrivedOn[0] == 0 ? "longer than the ride" : travelled));
                    }
                    rider.stopRiding();
                })
                .thenExecuteFor(40, () -> simulateTick(level, rider, cursor))
                .thenExecute(() -> rider.setPos(aside.getX() + 0.5, aside.getY(), aside.getZ() + 0.5))
                .thenExecuteFor(40, () -> simulateTick(level, rider, cursor))
                .thenExecute(() -> {
                    if (level.getBlockState(origin).getBlock() != plate) {
                        throw helper.assertionException("expected the %s back at %s, found %s"
                                .formatted(plate.getName().getString(), origin,
                                        level.getBlockState(origin).getBlock().getName().getString()));
                    }
                })
                .thenSucceed();
    }

    /** Gravity, standing on things, and the step-on trigger — the parts of a player that matter here. */
    private static void simulateTick(ServerLevel level, ServerPlayer rider, BlockPos.MutableBlockPos cursor) {
        if (rider.isPassenger()) {
            return; // the vehicle owns their position
        }

        double y = rider.getY() - 0.5;
        for (LiftPlateEntity plate : level.getEntitiesOfClass(LiftPlateEntity.class,
                new AABB(rider.getX() - 0.5, y - 1.0, rider.getZ() - 0.5,
                        rider.getX() + 0.5, rider.getY() + 0.1, rider.getZ() + 0.5))) {
            y = Math.max(y, plate.getBoundingBox().maxY);
        }
        BlockPos below = BlockPos.containing(rider.getX(), y - 0.01, rider.getZ());
        VoxelShape shape = level.getBlockState(below).getCollisionShape(level, below);
        if (!shape.isEmpty()) {
            y = Math.max(y, below.getY() + shape.max(Direction.Axis.Y));
        }
        rider.setPos(rider.getX(), y, rider.getZ());

        cursor.set(rider.getX(), rider.getY(), rider.getZ());
        BlockState state = level.getBlockState(cursor);
        if (state.getBlock() instanceof LiftPlateBlock plate) {
            plate.entityInside(state, level, cursor, rider, InsideBlockEffectApplier.NOOP, true);
        }
    }

    /// A lift only ever travels up and down its own shaft, so any horizontal drift at all is a bug.
    private static void assertStaysInItsColumn(GameTestHelper helper, BlockPos origin) {
        for (LiftPlateEntity plate : helper.getEntities(Lift.LIFT_PLATE_ENTITY)) {
            if (plate.getX() != origin.getX() + 0.5 || plate.getZ() != origin.getZ() + 0.5) {
                throw helper.assertionException("the plate left the column it was placed in, x=%.2f z=%.2f (expected %.2f, %.2f)"
                        .formatted(plate.getX(), plate.getZ(), origin.getX() + 0.5, origin.getZ() + 0.5));
            }
        }
    }

    private static List<BlockPos> findPlates(GameTestHelper helper) {
        List<BlockPos> found = new java.util.ArrayList<>();
        BlockPos.betweenClosed(helper.getBounds()).forEach(pos -> {
            if (helper.getLevel().getBlockState(pos).getBlock() instanceof LiftPlateBlock) {
                found.add(pos.immutable());
            }
        });
        return found;
    }
    /// A mock player with a working (embedded) network connection, unlike
    /// {@code makeMockServerPlayer}: real code paths send it packets — chat and overlay
    /// messages, cooldown and container syncs, ride teleports — and a player with a null
    /// connection throws on every one of them.
    /// ponytail: makeMockServerPlayerInLevel is deprecated for removal with nothing to
    /// replace it, and it is still the only helper that hands back a usable player. When it
    /// goes, build one here instead: a ServerPlayer plus a ServerGamePacketListenerImpl over
    /// a Connection with an EmbeddedChannel, registered through PlayerList.placeNewPlayer —
    /// which is all this method does today.
    @SuppressWarnings("removal")
    private static ServerPlayer mockPlayer(GameTestHelper helper, GameType gameType) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(gameType);
        return player;
    }
}
