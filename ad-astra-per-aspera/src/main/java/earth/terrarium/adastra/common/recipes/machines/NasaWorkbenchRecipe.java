package earth.terrarium.adastra.common.recipes.machines;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import earth.terrarium.adastra.common.recipes.base.CodecRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import earth.terrarium.adastra.common.registry.ModRecipeSerializers;
import earth.terrarium.adastra.common.registry.ModRecipeTypes;
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

public record NasaWorkbenchRecipe(
    List<Ingredient> ingredients,
    ItemStackTemplate template
) implements CodecRecipe<RecipeInput> {

    public ItemStack result() {
        return template.create();
    }


    public static final MapCodec<NasaWorkbenchRecipe> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(NasaWorkbenchRecipe::ingredients),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(NasaWorkbenchRecipe::template)
        ).apply(instance, NasaWorkbenchRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NasaWorkbenchRecipe> NETWORK_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
        NasaWorkbenchRecipe::ingredients,
        ItemStackTemplate.STREAM_CODEC,
        NasaWorkbenchRecipe::template,
        NasaWorkbenchRecipe::new
    );

    @Override
    public boolean matches(@NotNull RecipeInput container, @NotNull Level level) {
        if (container.size() < ingredients.size()) return false;
        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).test(container.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return ModRecipeSerializers.NASA_WORKBENCH_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipeTypes.NASA_WORKBENCH.get();
    }
}