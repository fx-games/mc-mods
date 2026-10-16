package earth.terrarium.adastra.common.recipes.machines;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import earth.terrarium.adastra.common.recipes.base.CodecRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import earth.terrarium.adastra.common.blockentities.base.ContainerRecipeWrapper;
import earth.terrarium.adastra.common.blockentities.machines.EtrionicBlastFurnaceBlockEntity;
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

import java.util.List;

public record AlloyingRecipe(
    int cookingTime, int energy,
    List<Ingredient> ingredients, ItemStackTemplate template
) implements CodecRecipe<RecipeInput> {

    public ItemStack result() {
        return template.create();
    }


    public static final MapCodec<AlloyingRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Codec.INT.fieldOf("cookingtime").forGetter(AlloyingRecipe::cookingTime),
            Codec.INT.fieldOf("energy").forGetter(AlloyingRecipe::energy),
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(AlloyingRecipe::ingredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(AlloyingRecipe::template)
        ).apply(instance, AlloyingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlloyingRecipe> NETWORK_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        AlloyingRecipe::cookingTime,
        ByteBufCodecs.INT,
        AlloyingRecipe::energy,
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
        AlloyingRecipe::ingredients,
        ItemStackTemplate.STREAM_CODEC,
        AlloyingRecipe::template,
        AlloyingRecipe::new
    );

    @Override
    public boolean matches(@NotNull RecipeInput recipeInput, @NotNull Level level) {
        if (recipeInput.size() < ingredients.size()) return false;
        for (int i = 0; i < Math.min(4, ingredients.size()); i++) {
            boolean found = false;
            for (int j = 0; j < 4; j++) {
                if (ingredients.get(i).test(recipeInput.getItem(j + 1))) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        if (!(recipeInput instanceof ContainerRecipeWrapper wrapper)) return false;
        if (!(wrapper.container() instanceof EtrionicBlastFurnaceBlockEntity entity)) return false;
        if (entity.getEnergyStorage().extract(energy, true) < energy) return false;
        return ItemUtils.canAddItem(recipeInput, result(), 5, 6, 7, 8);
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipeSerializers.ALLOYING.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipeTypes.ALLOYING.get();
    }
}
