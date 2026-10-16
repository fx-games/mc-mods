package earth.terrarium.adastra.common.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/** Fluid lookups. Our own blocks/items are used directly; everything else goes through the Fabric Transfer API. */
public final class FluidApi {
    private FluidApi() {}

    public static final Function<ItemContext, @Nullable CommonStorage<FluidResource>> ITEM = context -> {
        ItemStack stack = context.stack();
        if (stack.getItem() instanceof FluidProvider.Item provider) return provider.getFluids(stack, context);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, context.fabric());
        return storage == null ? null : new FabricBridge.FromFabricFluid(storage);
    };

    public static final Block BLOCK = new Block();

    public static final class Block {
        @Nullable
        public CommonStorage<FluidResource> find(BlockEntity entity, @Nullable Direction direction) {
            if (entity instanceof FluidProvider.BlockEntity provider) return provider.getFluids(direction);
            if (entity.getLevel() == null) return null;
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(entity.getLevel(), entity.getBlockPos(), entity.getBlockState(), entity, direction);
            return storage == null ? null : new FabricBridge.FromFabricFluid(storage);
        }

        public boolean isPresent(BlockEntity entity, @Nullable Direction direction) {
            return find(entity, direction) != null;
        }
    }

    /** Exposes Ad Astra block entities and items to other mods. */
    public static void register() {
        FluidStorage.SIDED.registerFallback((level, pos, state, entity, direction) -> {
            if (entity instanceof FluidProvider.BlockEntity provider) {
                CommonStorage<FluidResource> storage = provider.getFluids(direction);
                return storage == null ? null : new FabricBridge.ToFabricFluid(storage);
            }
            return null;
        });
        FluidStorage.ITEM.registerFallback((stack, context) -> {
            if (stack.getItem() instanceof FluidProvider.Item provider) {
                return new FabricBridge.ItemFluid(new FabricBridge.ItemBridge<>(context, c -> provider.getFluids(c.stack(), c)));
            }
            return null;
        });
    }
}
