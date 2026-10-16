package dev.explorercraft.starlight.mixin.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.explorercraft.starlight.client.StarField;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.world.level.MoonPhase;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Swaps vanilla's random star dome for the Kirchzarten sky. The sun and moon are untouched.
@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @Shadow
    @Final
    private RenderTarget renderTarget;

    @Unique
    private StarField starlight$field;
    /// renderStars only sees a pose already turned by vanilla's star angle, so the untouched
    /// pose and the angle are picked up one call earlier.
    @Unique
    private final Matrix4f starlight$pose = new Matrix4f();
    @Unique
    private float starlight$starAngle;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void starlight$build(TextureManager textureManager, AtlasManager atlasManager, RenderTarget renderTarget, CallbackInfo ci) {
        starlight$field = new StarField();
    }

    @Inject(method = "renderSunMoonAndStars", at = @At("HEAD"))
    private void starlight$capture(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase,
                                   float rainBrightness, float starBrightness, CallbackInfo ci) {
        starlight$pose.set(poseStack.last().pose());
        starlight$starAngle = starAngle;
    }

    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true)
    private void starlight$render(float starBrightness, PoseStack poseStack, CallbackInfo ci) {
        starlight$field.render(starlight$pose, starlight$starAngle, starBrightness, renderTarget);
        ci.cancel();
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void starlight$close(CallbackInfo ci) {
        starlight$field.close();
    }
}
