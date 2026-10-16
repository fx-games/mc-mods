package earth.terrarium.adastra.common.storage;

public interface ValueStorage {
    long getStoredAmount();

    long getCapacity();

    default boolean allowsInsertion() {
        return true;
    }

    default boolean allowsExtraction() {
        return true;
    }

    long insert(long amount, boolean simulate);

    long extract(long amount, boolean simulate);
}
