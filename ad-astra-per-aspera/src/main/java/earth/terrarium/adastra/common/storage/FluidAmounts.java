package earth.terrarium.adastra.common.storage;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

/** Fabric measures fluids in droplets (81000 per bucket). */
public final class FluidAmounts {
    public static final long BUCKET = FluidConstants.BUCKET;
    public static final long MILLIBUCKET = BUCKET / 1000;

    public static long toPlatformAmount(long millibuckets) {
        return millibuckets * MILLIBUCKET;
    }

    public static long toMillibuckets(long droplets) {
        return droplets / MILLIBUCKET;
    }
}
