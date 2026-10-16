package earth.terrarium.adastra.mixins.client;

import earth.terrarium.adastra.client.utils.DimensionRenderingUtils;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WeatherEffectRenderer.class)
public abstract class WeatherEffectRendererMixin {

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;", ordinal = 0))
    private Identifier adastra$acidRainTexture(Identifier texture) {
        return DimensionRenderingUtils.hasAcidRain() ? DimensionRenderingUtils.ACID_RAIN : texture;
    }
}
