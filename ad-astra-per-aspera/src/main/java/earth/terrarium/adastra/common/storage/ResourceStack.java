package earth.terrarium.adastra.common.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ResourceStack<T>(T resource, long amount) {
    public static final ResourceStack<FluidResource> EMPTY_FLUID = new ResourceStack<>(FluidResource.BLANK, 0);

    public static final Codec<ResourceStack<FluidResource>> FLUID_CODEC = RecordCodecBuilder.create(i -> i.group(
        FluidResource.CODEC.fieldOf("id").forGetter(ResourceStack::resource),
        Codec.LONG.fieldOf("amount").forGetter(ResourceStack::amount)
    ).apply(i, ResourceStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceStack<FluidResource>> FLUID_STREAM_CODEC = StreamCodec.composite(
        FluidResource.STREAM_CODEC, ResourceStack::resource,
        ByteBufCodecs.VAR_LONG, ResourceStack::amount,
        ResourceStack::new
    );

    public boolean isEmpty() {
        return amount <= 0 || (resource instanceof FluidResource f && f.isBlank());
    }

    public ResourceStack<T> withCount(long amount) {
        return new ResourceStack<>(resource, amount);
    }
}
