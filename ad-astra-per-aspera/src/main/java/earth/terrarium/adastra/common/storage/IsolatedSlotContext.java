package earth.terrarium.adastra.common.storage;

import net.minecraft.world.item.ItemStack;

/** Works on its own copy and allows the item to change (e.g. a water bucket becoming an empty bucket). */
public class IsolatedSlotContext extends ItemContext {
    public IsolatedSlotContext(ItemStack stack) {
        super(stack);
    }

    @Override
    protected void replace(ItemStack newStack) {
        this.stack = newStack;
    }
}
