package earth.terrarium.adastra.common.handlers.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.function.Supplier;

/**
 * Replacement for ResourcefulLib's removed {@code SaveHandler}: a {@link SavedData} stored as a raw compound tag.
 */
public abstract class SaveHandler extends SavedData {

    public abstract void loadData(CompoundTag tag);

    public abstract void saveData(CompoundTag tag);

    @Override
    public boolean isDirty() {
        return true;
    }

    public static <T extends SaveHandler> T read(SavedDataStorage storage, Supplier<T> factory, String id) {
        // Null DataFixTypes is handled by Fabric API (SavedDataStorageMixin).
        SavedDataType<T> type = new SavedDataType<>(Identifier.withDefaultNamespace(id), factory, CompoundTag.CODEC.xmap(tag -> {
            T handler = factory.get();
            handler.loadData(tag);
            return handler;
        }, handler -> {
            CompoundTag tag = new CompoundTag();
            handler.saveData(tag);
            return tag;
        }), null);
        return storage.computeIfAbsent(type);
    }
}
