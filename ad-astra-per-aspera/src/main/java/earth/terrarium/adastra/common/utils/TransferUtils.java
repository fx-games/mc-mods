package earth.terrarium.adastra.common.utils;

import earth.terrarium.adastra.common.blockentities.base.ContainerMachineBlockEntity;
import earth.terrarium.adastra.common.blockentities.base.sideconfig.Configuration;
import earth.terrarium.adastra.common.blockentities.base.sideconfig.ConfigurationEntry;
import earth.terrarium.adastra.common.storage.EnergyApi;
import earth.terrarium.adastra.common.storage.FluidApi;
import earth.terrarium.adastra.common.storage.ResourceStack;
import earth.terrarium.adastra.common.storage.FluidResource;
import earth.terrarium.adastra.common.storage.FluidAmounts;
import earth.terrarium.adastra.common.storage.CommonStorage;
import earth.terrarium.adastra.common.storage.UpdateManager;
import earth.terrarium.adastra.common.storage.ValueStorage;
import earth.terrarium.adastra.common.storage.TransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

import java.util.function.Predicate;

public class TransferUtils {

    public static void pushEnergyNearby(ContainerMachineBlockEntity machine, BlockPos pos, long amount, ConfigurationEntry sideConfig, Predicate<Direction> filter) {
        ValueStorage container = EnergyApi.BLOCK.find(machine, null);
        if (container == null) return;
        if (container.getStoredAmount() == 0) return;

        for (var entry : sideConfig.sides().entrySet()) {
            Configuration configuration = entry.getValue();
            if (!configuration.canPush()) continue;
            Direction direction = ModUtils.relative(machine, entry.getKey());
            if (!filter.test(direction)) continue;
            BlockEntity nearbyEntity = machine.level().getBlockEntity(pos.relative(direction));
            if (nearbyEntity == null) continue;
            ValueStorage nearbyContainer = EnergyApi.BLOCK.find(nearbyEntity, direction.getOpposite());
            if (nearbyContainer == null) continue;
            TransferUtil.moveValue(container, nearbyContainer, amount, false);
        }
    }

    public static void pullEnergyNearby(ContainerMachineBlockEntity machine, BlockPos pos, long amount, ConfigurationEntry sideConfig, Predicate<Direction> filter) {
        ValueStorage container = EnergyApi.BLOCK.find(machine, null);
        if (container == null) return;

        for (var entry : sideConfig.sides().entrySet()) {
            Configuration configuration = entry.getValue();
            if (!configuration.canPull()) continue;
            Direction direction = ModUtils.relative(machine, entry.getKey());
            if (!filter.test(direction)) continue;
            BlockEntity nearbyEntity = machine.level().getBlockEntity(pos.relative(direction));
            if (nearbyEntity == null) continue;
            ValueStorage nearbyContainer = EnergyApi.BLOCK.find(nearbyEntity, direction.getOpposite());
            if (nearbyContainer == null) continue;
            TransferUtil.moveValue(nearbyContainer, container, amount, false);
        }
    }

    public static void pushFluidNearby(ContainerMachineBlockEntity machine, BlockPos pos, CommonStorage<FluidResource> container, long amount, int tank, ConfigurationEntry sideConfig, Predicate<Direction> filter) {
        if (container.getContents(0).isEmpty()) return;

        for (var entry : sideConfig.sides().entrySet()) {
            Configuration configuration = entry.getValue();
            if (!configuration.canPush()) continue;
            Direction direction = ModUtils.relative(machine, entry.getKey());
            if (!filter.test(direction)) continue;
            BlockEntity nearbyEntity = machine.level().getBlockEntity(pos.relative(direction));
            if (nearbyEntity == null) continue;
            if (!FluidApi.BLOCK.isPresent(nearbyEntity, direction)) continue;
            CommonStorage<FluidResource> nearbyContainer = FluidApi.BLOCK.find(nearbyEntity, direction.getOpposite());
            if (nearbyContainer == null) continue;
            if (tank >= nearbyContainer.size()) continue;
            ResourceStack<FluidResource> holder = nearbyContainer.getContents(tank);
            if (holder.isEmpty()) continue;

            if (TransferUtil.move(nearbyContainer, container, holder.resource(), FluidAmounts.toMillibuckets(amount), false) > 0) {
                UpdateManager.batch(container, nearbyContainer);
                machine.sync();
            }
        }
    }

    public static void pullFluidNearby(ContainerMachineBlockEntity machine, BlockPos pos, CommonStorage<FluidResource> container, long amount, int tank, ConfigurationEntry sideConfig, Predicate<Direction> filter) {
        for (var entry : sideConfig.sides().entrySet()) {
            Configuration configuration = entry.getValue();
            if (!configuration.canPull()) continue;
            Direction direction = ModUtils.relative(machine, entry.getKey());
            if (!filter.test(direction)) continue;
            BlockEntity nearbyEntity = machine.level().getBlockEntity(pos.relative(direction));
            if (nearbyEntity == null) continue;
            if (!FluidApi.BLOCK.isPresent(nearbyEntity, direction)) continue;
            CommonStorage<FluidResource> nearbyContainer = FluidApi.BLOCK.find(nearbyEntity, direction.getOpposite());
            if (nearbyContainer == null) continue;
            if (tank >= container.size()) continue;
            ResourceStack<FluidResource> holder = container.getContents(tank);
            if (holder.isEmpty()) continue;
            if (TransferUtil.move(container, nearbyContainer, holder.resource(), FluidAmounts.toMillibuckets(amount), false) > 0) {
                UpdateManager.batch(container, nearbyContainer);
                machine.sync();
            }
        }
    }

    public static void pushItemsNearby(ContainerMachineBlockEntity machine, BlockPos pos, int[] slots, ConfigurationEntry sideConfig, Predicate<Direction> filter) {
        if (machine.isEmpty()) return;

        for (var entry : sideConfig.sides().entrySet()) {
            Configuration configuration = entry.getValue();
            if (!configuration.canPush()) continue;
            Direction direction = ModUtils.relative(machine, entry.getKey());
            if (!filter.test(direction)) continue;
            Container nearbyContainer = HopperBlockEntity.getContainerAt(machine.level(), pos.relative(direction));
            if (nearbyContainer == null) continue;
            ItemUtils.push(machine, nearbyContainer, slots, direction);
        }
    }

    public static void pullItemsNearby(ContainerMachineBlockEntity machine, BlockPos pos, int[] slots, ConfigurationEntry sideConfig, Predicate<Direction> filter) {
        for (var entry : sideConfig.sides().entrySet()) {
            Configuration configuration = entry.getValue();
            if (!configuration.canPull()) continue;
            Direction direction = ModUtils.relative(machine, entry.getKey());
            if (!filter.test(direction)) continue;
            Container nearbyContainer = HopperBlockEntity.getContainerAt(machine.level(), pos.relative(direction));
            if (nearbyContainer == null) continue;
            ItemUtils.pull(nearbyContainer, machine, slots, direction);
        }
    }
}
