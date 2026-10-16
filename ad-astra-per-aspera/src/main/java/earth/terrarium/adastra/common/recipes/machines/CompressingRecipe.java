package earth.terrarium.adastra.common.recipes.machines;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import earth.terrarium.adastra.common.recipes.base.CodecRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import earth.terrarium.adastra.common.blockentities.base.ContainerRecipeWrapper;
import earth.terrarium.adastra.common.blockentities.machines.CompressorBlockEntity;
import earth.terrarium.adastra.common.registry.ModRecipeSerializers;
import earth.terrarium.adastra.common.registry.ModRecipeTypes;
import earth.terrarium.adastra.common.utils.ItemUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record CompressingRecipe(
    int cookingTime, int energy,
    Ingredient ingredient, ItemStackTemplate template
) implements CodecRecipe<RecipeInput> {

    public ItemStack result() {
        return template.create();
    }


    public static final MapCodec<CompressingRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.INT.fieldOf("cookingtime").forGetter(CompressingRecipe::cookingTime),
            Codec.INT.fieldOf("energy").forGetter(CompressingRecipe::energy),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CompressingRecipe::ingredient),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(CompressingRecipe::template)
        ).apply(instance, CompressingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompressingRecipe> NETWORK_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        CompressingRecipe::cookingTime,
        ByteBufCodecs.INT,
        CompressingRecipe::energy,
        Ingredient.CONTENTS_STREAM_CODEC,
        CompressingRecipe::ingredient,
        ItemStackTemplate.STREAM_CODEC,
        CompressingRecipe::template,
        CompressingRecipe::new
    );

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        if (!(recipeInput instanceof ContainerRecipeWrapper wrapper)) return false;
        if (!(wrapper.container() instanceof CompressorBlockEntity entity)) return false;
        if (!ingredient.test(recipeInput.getItem(1))) return false;
        if (entity.getEnergyStorage().extract(energy, true) < energy) return false;
        return ItemUtils.canAddItem(recipeInput.getItem(2), result());
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipeSerializers.COMPRESSING.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipeTypes.COMPRESSING.get();
    }
}
