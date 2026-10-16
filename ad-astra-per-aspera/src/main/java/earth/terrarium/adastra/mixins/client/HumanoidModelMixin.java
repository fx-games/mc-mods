package earth.terrarium.adastra.mixins.client;

import earth.terrarium.adastra.client.AdAstraClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void adastra$setupAnimTail(HumanoidRenderState state, CallbackInfo ci) {
        Integer held = state.getData(AdAstraClient.HELD_OVER_HEAD);
        if (held == null) return;
        var model = (HumanoidModel<?>) (Object) this;
        model.rightArm.xRot = -2.8f;
        model.leftArm.xRot = -2.8f;
    }
}
