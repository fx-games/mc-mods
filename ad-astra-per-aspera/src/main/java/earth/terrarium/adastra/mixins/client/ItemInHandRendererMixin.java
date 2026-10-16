package earth.terrarium.adastra.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.adastra.client.renderers.ti69.Ti69Renderer;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, float inverseArmHeight, float attackValue, HumanoidArm arm);

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void adastra$submitArmWithItem(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack stack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, CallbackInfo ci) {
        if (stack.is(ModItems.TI_69.get())) {
            boolean mainHand = hand == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidArm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
            Ti69Renderer.renderTi69(poseStack, collector, lightCoords, inverseArmHeight, humanoidArm, attack, this::renderPlayerArm);
            ci.cancel();
        }
    }
}
