package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.blockentities.machines.OxygenDistributorBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class OxygenDistributorBlockEntityRenderer implements BlockEntityRenderer<OxygenDistributorBlockEntity, AnimatedBlockItemRenderer.State> {

    public static final Identifier TOP = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/oxygen_distributor_top");

    public OxygenDistributorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public AnimatedBlockItemRenderer.State createRenderState() {
        return new AnimatedBlockItemRenderer.State();
    }

    @Override
    public void extractRenderState(OxygenDistributorBlockEntity entity, AnimatedBlockItemRenderer.State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.blockState = entity.getBlockState();
        state.animation = Mth.lerp(partialTick, entity.lastYRot(), entity.yRot());
    }

    @Override
    public void submit(AnimatedBlockItemRenderer.State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        AnimatedBlockItemRenderer.applySided(state.blockState, poseStack);
        submitPart(state.blockState, state.animation, poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public static void submitPart(BlockState state, float yRot, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5, 0, -0.5);
        ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(TOP), poseStack, collector, light, overlay);
        poseStack.popPose();
    }
}
