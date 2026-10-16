package earth.terrarium.adastra.common.storage;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Function;

/** Energy lookups. Our own blocks/items are used directly; everything else goes through Team Reborn Energy. */
public final class EnergyApi {
    private EnergyApi() {}

    public static final Function<ItemContext, @Nullable ValueStorage> ITEM = context -> {
        ItemStack stack = context.stack();
        if (stack.getItem() instanceof EnergyProvider.Item provider) return provider.getEnergy(stack, context);
        EnergyStorage storage = EnergyStorage.ITEM.find(stack, context.fabric());
        return storage == null ? null : new FabricBridge.FromFabricEnergy(storage);
    };

    public static final Block BLOCK = new Block();

    public static final class Block {
        @Nullable
        public ValueStorage find(BlockEntity entity, @Nullable Direction direction) {
            if (entity instanceof EnergyProvider.BlockEntity provider) return provider.getEnergy(direction);
            if (entity.getLevel() == null) return null;
            EnergyStorage storage = EnergyStorage.SIDED.find(entity.getLevel(), entity.getBlockPos(), entity.getBlockState(), entity, direction);
            return storage == null ? null : new FabricBridge.FromFabricEnergy(storage);
        }

        public boolean isPresent(BlockEntity entity, @Nullable Direction direction) {
            return find(entity, direction) != null;
        }
    }

    /** Exposes Ad Astra block entities and items to other mods. */
    public static void register() {
        EnergyStorage.SIDED.registerFallback((level, pos, state, entity, direction) -> {
            if (entity instanceof EnergyProvider.BlockEntity provider) {
                ValueStorage storage = provider.getEnergy(direction);
                return storage == null ? null : new FabricBridge.ToFabricEnergy(storage);
            }
            return null;
        });
        EnergyStorage.ITEM.registerFallback((stack, context) -> {
            if (stack.getItem() instanceof EnergyProvider.Item provider) {
                return new FabricBridge.ItemEnergy(new FabricBridge.ItemBridge<>(context, c -> provider.getEnergy(c.stack(), c)));
            }
            return null;
        });
    }
}
