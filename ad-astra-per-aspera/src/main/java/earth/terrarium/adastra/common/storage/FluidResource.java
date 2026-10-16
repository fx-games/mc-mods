package earth.terrarium.adastra.common.storage;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

/** Thin wrapper around Fabric's {@link FluidVariant}, keeping the call sites ported from Common Storage Lib readable. */
public record FluidResource(FluidVariant variant) {
    public static final FluidResource BLANK = new FluidResource(FluidVariant.blank());
    public static final Codec<FluidResource> CODEC = Codec.withAlternative(
        FluidVariant.CODEC.xmap(FluidResource::of, FluidResource::variant),
        net.minecraft.core.registries.BuiltInRegistries.FLUID.byNameCodec().xmap(FluidResource::of, FluidResource::getType));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidResource> STREAM_CODEC = FluidVariant.PACKET_CODEC.map(FluidResource::of, FluidResource::variant);

    public static FluidResource of(Fluid fluid) {
        return of(FluidVariant.of(fluid));
    }

    public static FluidResource of(FluidVariant variant) {
        return variant.isBlank() ? BLANK : new FluidResource(variant);
    }

    public Fluid getType() {
        return variant.getFluid();
    }

    public boolean isBlank() {
        return variant.isBlank();
    }

    public boolean isOf(Fluid fluid) {
        return variant.isOf(fluid);
    }

    public boolean is(TagKey<Fluid> tag) {
        return !isBlank() && variant.typeHolder().is(tag);
    }
}
