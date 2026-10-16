package earth.terrarium.adastra.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import earth.terrarium.adastra.client.utils.DimensionRenderingUtils;
import earth.terrarium.adastra.common.registry.ModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @ModifyExpressionValue(method = "tickWeatherEffects", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;RAIN:Lnet/minecraft/core/particles/SimpleParticleType;"))
    private SimpleParticleType adastra$acidRainSplash(SimpleParticleType particle) {
        return DimensionRenderingUtils.hasAcidRain() ? ModParticleTypes.ACID_RAIN.get() : particle;
    }
}
