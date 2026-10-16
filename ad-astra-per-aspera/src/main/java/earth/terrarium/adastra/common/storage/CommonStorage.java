package earth.terrarium.adastra.common.storage;

public interface CommonStorage<T> extends StorageIO<T> {
    int size();

    StorageSlot<T> get(int index);

    default long getLimit(int index, T resource) {
        return get(index).getLimit(resource);
    }

    default T getResource(int index) {
        return get(index).getResource();
    }

    default ResourceStack<T> getContents(int index) {
        return get(index).getContents();
    }

    default long getAmount(int index) {
        return get(index).getAmount();
    }

    default long insert(int index, T resource, long amount, boolean simulate) {
        return get(index).insert(resource, amount, simulate);
    }

    default long extract(int index, T resource, long amount, boolean simulate) {
        return get(index).extract(resource, amount, simulate);
    }
}
