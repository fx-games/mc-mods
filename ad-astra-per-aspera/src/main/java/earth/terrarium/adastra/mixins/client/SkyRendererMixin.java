package earth.terrarium.adastra.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.adastra.client.dimension.ModSkyRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {

    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"), cancellable = true)
    private void adastra$renderSunMoonAndStars(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
        if (ModSkyRenderer.render(poseStack, sunAngle, rainBrightness, starBrightness)) ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void adastra$extractSunrise(ClientLevel level, float partialTicks, Camera camera, SkyRenderState state, CallbackInfo ci) {
        ModSkyRenderer.extractSunrise(state);
    }

    // The second rotation in renderSunriseAndSunset is the ZP one that picks the sunrise side.
    @ModifyArg(method = "renderSunriseAndSunset", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 1))
    private float adastra$sunriseAngle(float angle) {
        return angle + ModSkyRenderer.sunriseAngle();
    }
}
