package earth.terrarium.adastra.common.storage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Predicate;

/** A fluid tag plus an amount, e.g. {@code {"tag": "ad_astra:oil", "amount": 5}}. */
public record SizedFluidIngredient(TagKey<Fluid> tag, long amount) {
    public static final MapCodec<SizedFluidIngredient> FLAT_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(SizedFluidIngredient::tag),
        com.mojang.serialization.Codec.LONG.fieldOf("amount").forGetter(SizedFluidIngredient::amount)
    ).apply(i, SizedFluidIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> STREAM_CODEC = StreamCodec.composite(
        TagKey.streamCodec(Registries.FLUID), SizedFluidIngredient::tag,
        ByteBufCodecs.VAR_LONG, SizedFluidIngredient::amount,
        SizedFluidIngredient::new
    );

    public Predicate<FluidResource> ingredient() {
        return fluid -> fluid.is(tag);
    }

    public long getAmount() {
        return amount;
    }
}
