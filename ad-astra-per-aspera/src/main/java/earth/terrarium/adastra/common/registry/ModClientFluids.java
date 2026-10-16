package earth.terrarium.adastra.common.registry;

import com.teamresourceful.resourcefullib.client.fluid.data.ClientFluidProperties;
import com.teamresourceful.resourcefullib.client.registry.ResourcefulClientRegistries;
import com.teamresourceful.resourcefullib.client.registry.ResourcefulClientRegistryType;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.adastra.AdAstra;

public class ModClientFluids {
    public static final ResourcefulRegistry<ClientFluidProperties> CLIENT_FLUIDS = ResourcefulClientRegistries.create(ResourcefulClientRegistryType.FLUID, AdAstra.MOD_ID);

    public static final RegistryEntry<ClientFluidProperties> OXYGEN = CLIENT_FLUIDS.register("oxygen", () -> create(0xffdae6f0));
    public static final RegistryEntry<ClientFluidProperties> HYDROGEN = CLIENT_FLUIDS.register("hydrogen", () -> create(0xff89CFF0));
    public static final RegistryEntry<ClientFluidProperties> OIL = CLIENT_FLUIDS.register("oil", () -> create(0xff373A36));
    public static final RegistryEntry<ClientFluidProperties> FUEL = CLIENT_FLUIDS.register("fuel", () -> create(0xffE5292B));
    public static final RegistryEntry<ClientFluidProperties> CRYO_FUEL = CLIENT_FLUIDS.register("cryo_fuel", () -> create(0xff6cfffa));

    public static ClientFluidProperties create(int tintColor) {
        return ClientFluidProperties.builder()
            .still(ModFluids.STILL)
            .flowing(ModFluids.FLOWING)
            .overlay(ModFluids.OVERLAY)
            .screenOverlay(ModFluids.SCREEN_OVERLAY)
            .tintColor(tintColor)
            .build();
    }
}
