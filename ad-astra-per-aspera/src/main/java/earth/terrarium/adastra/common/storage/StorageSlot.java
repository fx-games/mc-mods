package earth.terrarium.adastra.common.storage;

public interface StorageSlot<T> extends StorageIO<T> {
    long getLimit(T resource);

    T getResource();

    long getAmount();

    boolean isResourceValid(T resource);

    default ResourceStack<T> getContents() {
        return new ResourceStack<>(getResource(), getAmount());
    }
}
