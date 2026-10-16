package earth.terrarium.adastra.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.adastra.client.models.armor.SpaceSuitModel;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.DyedItemColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Renders the space suit sleeve instead of the bare arm in first person.
 */
@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin {

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void adastra$renderHand(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, Identifier skinTexture, ModelPart arm, boolean hasSleeve, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        var stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(stack.getItem() instanceof SpaceSuitItem)) return;

        var layer = SpaceSuitModel.getLayerLocation(stack);
        var texture = SpaceSuitModel.getTextureLocation(stack);
        if (layer == null || texture == null) return;
        ci.cancel();

        var model = new SpaceSuitModel(Minecraft.getInstance().getEntityModels().bakeLayer(layer), EquipmentSlot.CHEST);
        // ponytail: the arm part passed in is the player model's; pick the matching suit arm by its x offset sign.
        boolean isRightHand = arm.x < 0;
        ModelPart suitArm = isRightHand ? model.rightArm : model.leftArm;
        arm.resetPose();
        suitArm.loadPose(arm.storePose());
        suitArm.zRot = isRightHand ? 0.1F : -0.1F;

        int color = stack.has(net.minecraft.core.component.DataComponents.DYED_COLOR)
            ? ARGB.opaque(DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR)) : -1;
        collector.submitModelPart(suitArm, poseStack, RenderTypes.entityTranslucent(texture), lightCoords, OverlayTexture.NO_OVERLAY, null, color, null);
    }
}
