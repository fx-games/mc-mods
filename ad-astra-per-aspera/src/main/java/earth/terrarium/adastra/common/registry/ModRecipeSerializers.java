package earth.terrarium.adastra.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.common.recipes.SpaceStationRecipe;
import earth.terrarium.adastra.common.recipes.machines.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Recipe;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ModRecipeSerializers {

    public static final ResourcefulRegistry<RecipeSerializer<?>> RECIPE_SERIALIZERS = ResourcefulRegistries.create(BuiltInRegistries.RECIPE_SERIALIZER, AdAstra.MOD_ID);

    public static final RegistryEntry<RecipeSerializer<CompressingRecipe>> COMPRESSING = RECIPE_SERIALIZERS.register("compressing", () ->
        sync(CompressingRecipe.CODEC, CompressingRecipe.NETWORK_CODEC));

    public static final RegistryEntry<RecipeSerializer<AlloyingRecipe>> ALLOYING = RECIPE_SERIALIZERS.register("alloying", () ->
        sync(AlloyingRecipe.CODEC, AlloyingRecipe.NETWORK_CODEC));

    public static final RegistryEntry<RecipeSerializer<OxygenLoadingRecipe>> OXYGEN_LOADING = RECIPE_SERIALIZERS.register("oxygen_loading", () ->
        sync(OxygenLoadingRecipe.CODEC, OxygenLoadingRecipe.NETWORK_CODEC));

    public static final RegistryEntry<RecipeSerializer<RefiningRecipe>> REFINING = RECIPE_SERIALIZERS.register("refining", () ->
        sync(RefiningRecipe.CODEC, RefiningRecipe.NETWORK_CODEC));

    public static final RegistryEntry<RecipeSerializer<CryoFreezingRecipe>> CRYO_FREEZING = RECIPE_SERIALIZERS.register("cryo_freezing", () ->
        sync(CryoFreezingRecipe.CODEC, CryoFreezingRecipe.NETWORK_CODEC));

    public static final RegistryEntry<RecipeSerializer<NasaWorkbenchRecipe>> NASA_WORKBENCH_SERIALIZER = RECIPE_SERIALIZERS.register("nasa_workbench", () ->
        sync(NasaWorkbenchRecipe.CODEC, NasaWorkbenchRecipe.NETWORK_CODEC));

    public static final RegistryEntry<RecipeSerializer<SpaceStationRecipe>> SPACE_STATION_SERIALIZER = RECIPE_SERIALIZERS.register("space_station_recipe", () ->
        sync(SpaceStationRecipe.CODEC, SpaceStationRecipe.NETWORK_CODEC));

    // Recipes are no longer sent to clients by default; machines and menus read them on both sides.
    private static <T extends Recipe<?>> RecipeSerializer<T> sync(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        var serializer = new RecipeSerializer<>(codec, streamCodec);
        RecipeSynchronization.synchronizeRecipeSerializer(serializer);
        return serializer;
    }
}
