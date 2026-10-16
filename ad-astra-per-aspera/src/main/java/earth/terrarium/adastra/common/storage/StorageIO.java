package earth.terrarium.adastra.common.storage;

public interface StorageIO<T> {
    long insert(T resource, long amount, boolean simulate);

    long extract(T resource, long amount, boolean simulate);
}
