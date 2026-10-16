package earth.terrarium.adastra.common.storage;

import net.minecraft.world.item.ItemStack;

/** Edits the components of the given stack in place; never swaps the item itself. */
public class ModifyOnlyContext extends ItemContext {
    public ModifyOnlyContext(ItemStack stack) {
        super(stack);
    }

    @Override
    protected void replace(ItemStack newStack) {
        if (newStack.is(stack.getItem()) && newStack.getCount() == stack.getCount()) {
            stack.applyComponents(newStack.getComponentsPatch());
        }
    }
}
