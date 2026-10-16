package earth.terrarium.adastra.common.storage;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Adapters between the simple (simulate-flag based) storages used inside Ad Astra and the
 * transactional Fabric Transfer API / Team Reborn Energy API used by everyone else.
 */
final class FabricBridge {
    private FabricBridge() {}

    static Transaction open() {
        return Transaction.openNested(Transaction.getCurrentUnsafe());
    }

    /** Runs {@code undo} if the change made in {@code tx} ends up being rolled back, at any nesting level. */
    static void onAbort(TransactionContext tx, Runnable undo) {
        tx.addCloseCallback((closed, result) -> {
            if (result.wasAborted()) {
                undo.run();
            } else if (closed.nestingDepth() > 0) {
                onAbort(closed.getOpenTransaction(closed.nestingDepth() - 1), undo);
            }
        });
    }

    static void onCommit(TransactionContext tx, Object manager) {
        tx.addOuterCloseCallback(result -> {
            if (result.wasCommitted()) UpdateManager.batch(manager);
        });
    }

    // ---- foreign -> Ad Astra ----

    record FromFabricEnergy(EnergyStorage storage) implements ValueStorage {
        @Override
        public long getStoredAmount() {
            return storage.getAmount();
        }

        @Override
        public long getCapacity() {
            return storage.getCapacity();
        }

        @Override
        public boolean allowsInsertion() {
            return storage.supportsInsertion();
        }

        @Override
        public boolean allowsExtraction() {
            return storage.supportsExtraction();
        }

        @Override
        public long insert(long amount, boolean simulate) {
            try (Transaction tx = open()) {
                long inserted = storage.insert(amount, tx);
                if (!simulate) tx.commit();
                return inserted;
            }
        }

        @Override
        public long extract(long amount, boolean simulate) {
            try (Transaction tx = open()) {
                long extracted = storage.extract(amount, tx);
                if (!simulate) tx.commit();
                return extracted;
            }
        }
    }

    static final class FromFabricFluid implements CommonStorage<FluidResource> {
        private final Storage<FluidVariant> storage;
        private final List<StorageSlot<FluidResource>> slots = new ArrayList<>();

        FromFabricFluid(Storage<FluidVariant> storage) {
            this.storage = storage;
            for (StorageView<FluidVariant> view : storage.nonEmptyViews()) slots.add(new ViewSlot(view));
            for (StorageView<FluidVariant> view : storage) {
                if (view.isResourceBlank() && slots.size() < 8) slots.add(new ViewSlot(view));
            }
            if (slots.isEmpty()) slots.add(new ViewSlot(null));
        }

        @Override
        public int size() {
            return slots.size();
        }

        @Override
        public StorageSlot<FluidResource> get(int index) {
            return slots.get(index);
        }

        @Override
        public long insert(FluidResource resource, long amount, boolean simulate) {
            if (resource.isBlank() || amount <= 0) return 0;
            try (Transaction tx = open()) {
                long inserted = storage.insert(resource.variant(), amount, tx);
                if (!simulate) tx.commit();
                return inserted;
            }
        }

        @Override
        public long extract(FluidResource resource, long amount, boolean simulate) {
            if (resource.isBlank() || amount <= 0) return 0;
            try (Transaction tx = open()) {
                long extracted = storage.extract(resource.variant(), amount, tx);
                if (!simulate) tx.commit();
                return extracted;
            }
        }

        private final class ViewSlot implements StorageSlot<FluidResource> {
            private final StorageView<FluidVariant> view;

            ViewSlot(StorageView<FluidVariant> view) {
                this.view = view;
            }

            @Override
            public long getLimit(FluidResource resource) {
                return view == null ? 0 : view.getCapacity();
            }

            @Override
            public FluidResource getResource() {
                return view == null ? FluidResource.BLANK : FluidResource.of(view.getResource());
            }

            @Override
            public long getAmount() {
                return view == null || view.isResourceBlank() ? 0 : view.getAmount();
            }

            @Override
            public boolean isResourceValid(FluidResource resource) {
                return true;
            }

            @Override
            public long insert(FluidResource resource, long amount, boolean simulate) {
                return FromFabricFluid.this.insert(resource, amount, simulate);
            }

            @Override
            public long extract(FluidResource resource, long amount, boolean simulate) {
                if (view == null || resource.isBlank() || amount <= 0) return 0;
                try (Transaction tx = open()) {
                    long extracted = view.extract(resource.variant(), amount, tx);
                    if (!simulate) tx.commit();
                    return extracted;
                }
            }
        }
    }

    // ---- Ad Astra -> foreign ----

    record ToFabricEnergy(ValueStorage storage) implements EnergyStorage {
        @Override
        public boolean supportsInsertion() {
            return storage.allowsInsertion();
        }

        @Override
        public boolean supportsExtraction() {
            return storage.allowsExtraction();
        }

        @Override
        public long insert(long maxAmount, TransactionContext tx) {
            if (!storage.allowsInsertion()) return 0;
            long inserted = storage.insert(maxAmount, false);
            if (inserted > 0) {
                onAbort(tx, () -> storage.extract(inserted, false));
                onCommit(tx, storage);
            }
            return inserted;
        }

        @Override
        public long extract(long maxAmount, TransactionContext tx) {
            if (!storage.allowsExtraction()) return 0;
            long extracted = storage.extract(maxAmount, false);
            if (extracted > 0) {
                onAbort(tx, () -> storage.insert(extracted, false));
                onCommit(tx, storage);
            }
            return extracted;
        }

        @Override
        public long getAmount() {
            return storage.getStoredAmount();
        }

        @Override
        public long getCapacity() {
            return storage.getCapacity();
        }
    }

    record ToFabricFluid(CommonStorage<FluidResource> storage) implements Storage<FluidVariant> {
        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext tx) {
            FluidResource fluid = FluidResource.of(resource);
            long inserted = storage.insert(fluid, maxAmount, false);
            if (inserted > 0) {
                onAbort(tx, () -> storage.extract(fluid, inserted, false));
                onCommit(tx, storage);
            }
            return inserted;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
            long total = 0;
            for (int i = 0; i < storage.size() && total < maxAmount; i++) {
                total += extractSlot(i, resource, maxAmount - total, tx);
            }
            return total;
        }

        private long extractSlot(int index, FluidVariant resource, long maxAmount, TransactionContext tx) {
            FluidResource fluid = FluidResource.of(resource);
            StorageSlot<FluidResource> slot = storage.get(index);
            long extracted = slot.extract(fluid, maxAmount, false);
            if (extracted > 0) {
                onAbort(tx, () -> slot.insert(fluid, extracted, false));
                onCommit(tx, storage);
            }
            return extracted;
        }

        @Override
        public @NotNull Iterator<StorageView<FluidVariant>> iterator() {
            List<StorageView<FluidVariant>> views = new ArrayList<>(storage.size());
            for (int i = 0; i < storage.size(); i++) {
                int index = i;
                views.add(new StorageView<>() {
                    @Override
                    public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
                        return extractSlot(index, resource, maxAmount, tx);
                    }

                    @Override
                    public boolean isResourceBlank() {
                        return storage.getResource(index).isBlank();
                    }

                    @Override
                    public FluidVariant getResource() {
                        return storage.getResource(index).variant();
                    }

                    @Override
                    public long getAmount() {
                        return storage.getAmount(index);
                    }

                    @Override
                    public long getCapacity() {
                        return storage.getLimit(index, storage.getResource(index));
                    }
                });
            }
            return views.iterator();
        }
    }

    /** Exposes one of our items to Fabric: every change is applied to a copy, then swapped into the Fabric context. */
    static final class ItemBridge<S> {
        private final ContainerItemContext context;
        private final Function<ModifyOnlyContext, S> factory;

        ItemBridge(ContainerItemContext context, Function<ModifyOnlyContext, S> factory) {
            this.context = context;
            this.factory = factory;
        }

        S read() {
            return factory.apply(new ModifyOnlyContext(context.getItemVariant().toStack(1)));
        }

        long modify(Function<S, Long> action, TransactionContext tx) {
            if (context.getItemVariant().isBlank() || context.getAmount() < 1) return 0;
            ItemStack copy = context.getItemVariant().toStack(1);
            long moved = action.apply(factory.apply(new ModifyOnlyContext(copy)));
            if (moved <= 0) return 0;
            return context.exchange(ItemVariant.of(copy), 1, tx) == 1 ? moved : 0;
        }
    }

    record ItemEnergy(ItemBridge<ValueStorage> bridge) implements EnergyStorage {
        @Override
        public long insert(long maxAmount, TransactionContext tx) {
            return bridge.modify(s -> s.insert(maxAmount, false), tx);
        }

        @Override
        public long extract(long maxAmount, TransactionContext tx) {
            return bridge.modify(s -> s.extract(maxAmount, false), tx);
        }

        @Override
        public long getAmount() {
            return bridge.read().getStoredAmount();
        }

        @Override
        public long getCapacity() {
            return bridge.read().getCapacity();
        }
    }

    record ItemFluid(ItemBridge<CommonStorage<FluidResource>> bridge) implements Storage<FluidVariant> {
        @Override
        public long insert(FluidVariant resource, long maxAmount, TransactionContext tx) {
            return bridge.modify(s -> s.insert(FluidResource.of(resource), maxAmount, false), tx);
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
            return bridge.modify(s -> s.extract(FluidResource.of(resource), maxAmount, false), tx);
        }

        @Override
        public @NotNull Iterator<StorageView<FluidVariant>> iterator() {
            CommonStorage<FluidResource> snapshot = bridge.read();
            List<StorageView<FluidVariant>> views = new ArrayList<>(snapshot.size());
            for (int i = 0; i < snapshot.size(); i++) {
                ResourceStack<FluidResource> contents = snapshot.getContents(i);
                long capacity = snapshot.getLimit(i, contents.resource());
                views.add(new StorageView<>() {
                    @Override
                    public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
                        return ItemFluid.this.extract(resource, maxAmount, tx);
                    }

                    @Override
                    public boolean isResourceBlank() {
                        return contents.isEmpty();
                    }

                    @Override
                    public FluidVariant getResource() {
                        return contents.resource().variant();
                    }

                    @Override
                    public long getAmount() {
                        return contents.amount();
                    }

                    @Override
                    public long getCapacity() {
                        return capacity;
                    }
                });
            }
            return views.iterator();
        }
    }
}
