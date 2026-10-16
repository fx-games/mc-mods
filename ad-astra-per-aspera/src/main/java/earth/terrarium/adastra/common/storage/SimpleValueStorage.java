package earth.terrarium.adastra.common.storage;

import net.minecraft.core.component.DataComponentType;

public class SimpleValueStorage implements ValueStorage, UpdateManager<Long> {
    private final long capacity;
    private final Runnable update;
    private final Runnable save;
    private long amount;

    public SimpleValueStorage(long capacity) {
        this.capacity = capacity;
        this.update = () -> {};
        this.save = () -> {};
    }

    public SimpleValueStorage(ItemContext context, DataComponentType<Long> componentType, long capacity) {
        this.capacity = capacity;
        this.update = context::updateAll;
        this.save = () -> context.set(componentType, this.amount);
        this.amount = context.stack().getOrDefault(componentType, 0L);
    }

    public SimpleValueStorage(Object holder, DataManager<Long> manager, long capacity) {
        this.capacity = capacity;
        this.update = () -> manager.set(holder, this.amount);
        this.save = () -> {};
        this.amount = manager.get(holder);
    }

    @Override
    public long getStoredAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    public void set(long amount) {
        this.amount = amount;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        long inserted = Math.max(0, Math.min(amount, capacity - this.amount));
        if (inserted > 0 && !simulate) {
            this.amount += inserted;
            save.run();
        }
        return inserted;
    }

    @Override
    public long extract(long amount, boolean simulate) {
        long extracted = Math.max(0, Math.min(amount, this.amount));
        if (extracted > 0 && !simulate) {
            this.amount -= extracted;
            save.run();
        }
        return extracted;
    }

    @Override
    public Long createSnapshot() {
        return amount;
    }

    @Override
    public void readSnapshot(Long snapshot) {
        this.amount = snapshot;
    }

    @Override
    public void update() {
        update.run();
    }
}
