package earth.terrarium.adastra.common.storage;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class FluidProvider {
    private FluidProvider() {}

    public interface BlockEntity {
        CommonStorage<FluidResource> getFluids(@Nullable Direction direction);
    }

    public interface Entity {
        CommonStorage<FluidResource> getFluids(@Nullable Direction direction);
    }

    public interface Item {
        CommonStorage<FluidResource> getFluids(ItemStack stack, ItemContext context);
    }
}
