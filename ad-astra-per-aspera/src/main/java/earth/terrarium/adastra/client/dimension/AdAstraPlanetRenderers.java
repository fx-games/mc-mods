package earth.terrarium.adastra.client.dimension;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads planet renderer definitions, keyed by dimension id (dimension types no longer carry an effects id).
 */
public class AdAstraPlanetRenderers extends SimpleJsonResourceReloadListener<PlanetRenderer> {

    private static Map<ResourceKey<Level>, PlanetRenderer> renderers = Map.of();

    public AdAstraPlanetRenderers() {
        super(PlanetRenderer.CODEC, FileToIdConverter.json("planet_renderers"));
    }

    @Override
    protected void apply(Map<Identifier, PlanetRenderer> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceKey<Level>, PlanetRenderer> map = new HashMap<>();
        object.values().forEach(renderer -> map.put(renderer.dimension(), renderer));
        renderers = map;
    }

    @Nullable
    public static PlanetRenderer get(ResourceKey<Level> dimension) {
        return renderers.get(dimension);
    }
}
