package earth.terrarium.adastra.common.storage;

public interface UpdateManager<T> {
    T createSnapshot();

    void readSnapshot(T snapshot);

    void update();

    static void batch(Object... managers) {
        for (Object potential : managers) {
            if (potential instanceof UpdateManager<?> manager) manager.update();
        }
    }
}
