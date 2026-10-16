package earth.terrarium.adastra.common.container;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;

/**
 * Simple container that retains the order stacks were inserted in.
 */
public class VehicleContainer extends SimpleContainer {

    public VehicleContainer(int size) {
        super(size);
    }

    @Override
    public void fromItemList(ValueInput.TypedInputList<ItemStack> items) {
        clearContent();
        int i = 0;
        for (ItemStack stack : items) {
            if (i >= getContainerSize()) break;
            setItem(i++, stack);
        }
    }
}
