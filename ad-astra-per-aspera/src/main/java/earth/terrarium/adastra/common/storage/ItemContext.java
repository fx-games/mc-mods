package earth.terrarium.adastra.common.storage;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A mutable holder for a single stack that item storages read from and write into.
 * {@link #fabric()} exposes it as a Fabric {@link ContainerItemContext} so foreign items (buckets, other mods) work too.
 */
public abstract class ItemContext {
    protected ItemStack stack;

    protected ItemContext(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack stack() {
        return stack;
    }

    public <T> void set(DataComponentType<T> type, T value) {
        stack.set(type, value);
    }

    public void updateAll() {}

    /** Called when a Fabric storage exchanges the held item for another one. */
    protected abstract void replace(ItemStack newStack);

    @Nullable
    public <T> T find(Function<ItemContext, @Nullable T> lookup) {
        return stack.isEmpty() ? null : lookup.apply(this);
    }

    public boolean isPresent(Function<ItemContext, ?> lookup) {
        return find(lookup) != null;
    }

    public ContainerItemContext fabric() {
        return ContainerItemContext.ofSingleSlot(new SingleStackStorage() {
            @Override
            protected ItemStack getStack() {
                return stack;
            }

            @Override
            protected void setStack(ItemStack newStack) {
                replace(newStack);
            }
        });
    }
}
