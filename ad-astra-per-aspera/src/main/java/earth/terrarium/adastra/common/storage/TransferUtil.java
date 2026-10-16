package earth.terrarium.adastra.common.storage;

public final class TransferUtil {
    private TransferUtil() {}

    public static <T> long move(StorageIO<T> from, StorageIO<T> to, T resource, long amount, boolean simulate) {
        long moved = Math.min(from.extract(resource, amount, true), to.insert(resource, amount, true));
        if (moved <= 0) return 0;
        if (from.extract(resource, moved, true) != moved || to.insert(resource, moved, true) != moved) return 0;
        if (!simulate) {
            from.extract(resource, moved, false);
            to.insert(resource, moved, false);
            UpdateManager.batch(from, to);
        }
        return moved;
    }

    public static long moveValue(ValueStorage from, ValueStorage to, long amount, boolean simulate) {
        long moved = Math.min(from.extract(amount, true), to.insert(amount, true));
        if (moved <= 0) return 0;
        if (from.extract(moved, true) != moved || to.insert(moved, true) != moved) return 0;
        if (!simulate) {
            from.extract(moved, false);
            to.insert(moved, false);
            UpdateManager.batch(from, to);
        }
        return moved;
    }

    public static <T> long insertSlots(CommonStorage<T> container, T resource, long amount, boolean simulate) {
        long inserted = 0;
        // Top up slots that already hold something first, then fill empty ones.
        for (int pass = 0; pass < 2 && inserted < amount; pass++) {
            for (int i = 0; i < container.size() && inserted < amount; i++) {
                StorageSlot<T> slot = container.get(i);
                if ((slot.getAmount() > 0) == (pass == 0)) inserted += slot.insert(resource, amount - inserted, simulate);
            }
        }
        return inserted;
    }

    public static <T> long extractSlots(CommonStorage<T> container, T resource, long amount, boolean simulate) {
        long extracted = 0;
        for (int i = 0; i < container.size() && extracted < amount; i++) {
            extracted += container.get(i).extract(resource, amount - extracted, simulate);
        }
        return extracted;
    }
}
