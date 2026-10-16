package earth.terrarium.adastra.common.storage;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record FluidStorageData(List<ResourceStack<FluidResource>> stacks) {
    public static final FluidStorageData EMPTY = new FluidStorageData(List.of());
    public static final Codec<FluidStorageData> CODEC = ResourceStack.FLUID_CODEC.listOf().xmap(FluidStorageData::new, FluidStorageData::stacks);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStorageData> NETWORK_CODEC = ResourceStack.FLUID_STREAM_CODEC
        .apply(ByteBufCodecs.list())
        .map(FluidStorageData::new, FluidStorageData::stacks);

    public static FluidStorageData from(CommonStorage<FluidResource> container) {
        List<ResourceStack<FluidResource>> stacks = new ArrayList<>(container.size());
        for (int i = 0; i < container.size(); i++) stacks.add(container.getContents(i));
        return new FluidStorageData(List.copyOf(stacks));
    }
}
