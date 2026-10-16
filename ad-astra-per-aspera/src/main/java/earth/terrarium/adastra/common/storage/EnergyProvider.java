package earth.terrarium.adastra.common.storage;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class EnergyProvider {
    private EnergyProvider() {}

    public interface BlockEntity {
        ValueStorage getEnergy(@Nullable Direction direction);
    }

    public interface Item {
        ValueStorage getEnergy(ItemStack stack, ItemContext context);
    }
}
