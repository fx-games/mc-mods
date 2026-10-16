package earth.terrarium.adastra.common.registry;

import com.mojang.serialization.Codec;
import earth.terrarium.adastra.common.storage.DataManager;
import earth.terrarium.adastra.common.storage.FluidStorageData;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModDataManagers {
    public static final DataManager<FluidStorageData> FLUID_CONTENTS = new DataManager<>("fluids", FluidStorageData.CODEC, FluidStorageData.NETWORK_CODEC, () -> FluidStorageData.EMPTY);
    public static final DataManager<Long> VALUE_CONTENT = new DataManager<>("energy", Codec.LONG, ByteBufCodecs.VAR_LONG, () -> 0L);

    public static void init() {}
}
