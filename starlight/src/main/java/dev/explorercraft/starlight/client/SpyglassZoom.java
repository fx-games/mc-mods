package dev.explorercraft.starlight.client;

/// The spyglass's field-of-view factor, which the scroll wheel changes while scoping. Vanilla
/// fixes it at 0.1 (10x); the setting is kept between uses.
public final class SpyglassZoom {
    public static final float VANILLA = 0.1f;
    private static final float WIDEST = 0.5f;   // 2x, enough to frame a whole constellation
    private static final float NARROWEST = 0.02f; // 50x
    private static final float STEP = 0.8f;

    private static float factor = VANILLA;
    /// When the zoom last changed, for the HUD readout.
    public static long changedAtMillis;

    private SpyglassZoom() {
    }

    public static float factor() {
        return factor;
    }

    /// Positive notches zoom in.
    public static void scroll(int notches) {
        factor = Math.clamp(factor * (float) Math.pow(STEP, notches), NARROWEST, WIDEST);
        changedAtMillis = System.currentTimeMillis();
    }

    public static int magnification() {
        return Math.round(1 / factor);
    }
}
