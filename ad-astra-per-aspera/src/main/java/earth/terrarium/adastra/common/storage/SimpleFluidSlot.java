package earth.terrarium.adastra.common.storage;

import java.util.function.Predicate;

public class SimpleFluidSlot implements StorageSlot<FluidResource> {
    private final long limit;
    private final Runnable save;
    private final Predicate<FluidResource> filter;
    private FluidResource resource = FluidResource.BLANK;
    private long amount;

    public SimpleFluidSlot(long limit, Runnable save, Predicate<FluidResource> filter) {
        this.limit = limit;
        this.save = save;
        this.filter = filter;
    }

    @Override
    public long getLimit(FluidResource resource) {
        return limit;
    }

    @Override
    public boolean isResourceValid(FluidResource resource) {
        return filter.test(resource);
    }

    @Override
    public FluidResource getResource() {
        return resource;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long insert(FluidResource resource, long amount, boolean simulate) {
        if (resource.isBlank() || amount <= 0 || !isResourceValid(resource)) return 0;
        if (!this.resource.isBlank() && !this.resource.equals(resource)) return 0;
        long inserted = Math.min(amount, limit - this.amount);
        if (inserted > 0 && !simulate) {
            this.resource = resource;
            this.amount += inserted;
            save.run();
        }
        return inserted;
    }

    @Override
    public long extract(FluidResource resource, long amount, boolean simulate) {
        if (this.resource.isBlank() || !this.resource.equals(resource)) return 0;
        long extracted = Math.min(amount, this.amount);
        if (extracted > 0 && !simulate) {
            this.amount -= extracted;
            if (this.amount == 0) this.resource = FluidResource.BLANK;
            save.run();
        }
        return extracted;
    }

    public void readSnapshot(ResourceStack<FluidResource> snapshot) {
        boolean empty = snapshot.isEmpty();
        this.resource = empty ? FluidResource.BLANK : snapshot.resource();
        this.amount = empty ? 0 : snapshot.amount();
    }
}
