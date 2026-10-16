package earth.terrarium.adastra.common.menus.configuration;

import earth.terrarium.adastra.common.blockentities.base.sideconfig.ConfigurationType;
import earth.terrarium.adastra.common.storage.FluidResource;
import earth.terrarium.adastra.common.storage.CommonStorage;

public record FluidConfiguration(
    int index,
    int x,
    int y,
    CommonStorage<FluidResource> container,
    int tank
) implements MenuConfiguration {

    @Override
    public ConfigurationType type() {
        return ConfigurationType.FLUID;
    }
}
