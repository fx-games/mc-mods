package earth.terrarium.adastra.common.storage;

import net.minecraft.core.component.DataComponentType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SimpleFluidStorage implements CommonStorage<FluidResource>, UpdateManager<FluidStorageData> {
    private final List<SimpleFluidSlot> slots = new ArrayList<>();
    private final long limit;
    private final Runnable update;
    private final Runnable save;

    public SimpleFluidStorage(int size, long limit) {
        this.limit = limit;
        this.update = () -> {};
        this.save = () -> {};
        fill(size);
    }

    /** Item-backed storage: contents live in a data component on the stack of the context. */
    public SimpleFluidStorage(ItemContext context, DataComponentType<FluidStorageData> componentType, int size, long limit) {
        this.limit = limit;
        this.update = context::updateAll;
        this.save = () -> context.set(componentType, FluidStorageData.from(this));
        fill(size);
        FluidStorageData data = context.stack().get(componentType);
        if (data != null) readSnapshot(data);
    }

    /** Block entity / entity backed storage, synced to clients through a {@link DataManager}. */
    public SimpleFluidStorage(Object holder, DataManager<FluidStorageData> manager, int size, long limit) {
        this.limit = limit;
        this.update = () -> manager.set(holder, FluidStorageData.from(this));
        this.save = () -> {};
        fill(size);
        readSnapshot(manager.get(holder));
    }

    private void fill(int size) {
        for (int i = 0; i < size; i++) slots.add(new SimpleFluidSlot(limit, this::save, f -> true));
    }

    private void save() {
        save.run();
    }

    public SimpleFluidStorage filter(int slot, Predicate<FluidResource> predicate) {
        SimpleFluidSlot newSlot = new SimpleFluidSlot(limit, this::save, predicate);
        newSlot.readSnapshot(slots.get(slot).getContents());
        slots.set(slot, newSlot);
        return this;
    }

    @Override
    public int size() {
        return slots.size();
    }

    @Override
    public SimpleFluidSlot get(int index) {
        return slots.get(index);
    }

    @Override
    public FluidStorageData createSnapshot() {
        return FluidStorageData.from(this);
    }

    @Override
    public void readSnapshot(FluidStorageData snapshot) {
        for (int i = 0; i < slots.size() && i < snapshot.stacks().size(); i++) {
            slots.get(i).readSnapshot(snapshot.stacks().get(i));
        }
    }

    @Override
    public void update() {
        update.run();
    }

    @Override
    public long insert(FluidResource resource, long amount, boolean simulate) {
        return TransferUtil.insertSlots(this, resource, amount, simulate);
    }

    @Override
    public long extract(FluidResource resource, long amount, boolean simulate) {
        return TransferUtil.extractSlots(this, resource, amount, simulate);
    }
}
