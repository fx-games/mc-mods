package earth.terrarium.adastra.mixins.client;

import earth.terrarium.adastra.client.utils.DimensionRenderingUtils;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Loads venus_clouds.png next to the vanilla cloud texture and swaps the cell data in acid rain biomes.
 */
@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {

    @Shadow
    private CloudRenderer.@Nullable TextureData texture;
    @Shadow
    private boolean needsRebuild;

    @Shadow
    protected abstract Optional<CloudRenderer.TextureData> prepare(ResourceManager manager, ProfilerFiller profiler);

    @Unique
    private boolean adastra$loadingVenus;
    @Unique
    private CloudRenderer.@Nullable TextureData adastra$vanillaTexture;
    @Unique
    private CloudRenderer.@Nullable TextureData adastra$venusTexture;

    @ModifyArg(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceManager;open(Lnet/minecraft/resources/Identifier;)Ljava/io/InputStream;"))
    private Identifier adastra$venusCloudTexture(Identifier location) {
        return adastra$loadingVenus ? DimensionRenderingUtils.VENUS_CLOUDS : location;
    }

    @Inject(method = "apply(Ljava/util/Optional;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void adastra$apply(Optional<CloudRenderer.TextureData> preparations, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) {
        adastra$vanillaTexture = texture;
        // ponytail: decoded on the apply thread; it's one small png
        adastra$loadingVenus = true;
        adastra$venusTexture = prepare(manager, profiler).orElse(null);
        adastra$loadingVenus = false;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void adastra$render(int color, CloudStatus cloudStatus, float bottomY, int range, Vec3 cameraPosition, long gameTime, float partialTicks, CallbackInfo ci) {
        var wanted = DimensionRenderingUtils.hasAcidRain() && adastra$venusTexture != null ? adastra$venusTexture : adastra$vanillaTexture;
        if (wanted != texture) {
            texture = wanted;
            needsRebuild = true;
        }
    }
}
