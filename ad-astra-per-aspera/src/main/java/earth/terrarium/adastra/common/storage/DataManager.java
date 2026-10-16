package earth.terrarium.adastra.common.storage;

import com.mojang.serialization.Codec;
import earth.terrarium.adastra.AdAstra;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

/** Synced data attached to (block) entities, plus a matching item component. Backed by the Fabric attachment API. */
public final class DataManager<T> {
    private final AttachmentType<T> attachment;
    private final DataComponentType<T> component;
    private final Supplier<T> defaultValue;

    public DataManager(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> defaultValue) {
        Identifier id = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, name);
        this.defaultValue = defaultValue;
        this.attachment = AttachmentRegistry.create(id, builder -> builder
            .initializer(defaultValue)
            .persistent(codec)
            .copyOnDeath()
            .syncWith(streamCodec, AttachmentSyncPredicate.all()));
        this.component = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id,
            DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
    }

    public T get(Object holder) {
        return Objects.requireNonNullElseGet(((AttachmentTarget) holder).getAttached(attachment), defaultValue);
    }

    public void set(Object holder, T value) {
        AttachmentTarget target = (AttachmentTarget) holder;
        if (!Objects.equals(target.getAttached(attachment), value)) target.setAttached(attachment, value);
    }

    public DataComponentType<T> componentType() {
        return component;
    }
}
