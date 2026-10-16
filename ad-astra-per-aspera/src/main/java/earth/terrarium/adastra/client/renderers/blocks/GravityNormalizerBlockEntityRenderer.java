package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.blockentities.machines.GravityNormalizerBlockEntity;
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
import org.joml.Quaternionf;

public class GravityNormalizerBlockEntityRenderer implements BlockEntityRenderer<GravityNormalizerBlockEntity, AnimatedBlockItemRenderer.State> {

    public static final Identifier TOP = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/gravity_normalizer_top");
    public static final Identifier TOE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/gravity_normalizer_toe");

    private static final float SIN_45 = (float) Math.sin(Math.PI / 4);

    public GravityNormalizerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public AnimatedBlockItemRenderer.State createRenderState() {
        return new AnimatedBlockItemRenderer.State();
    }

    @Override
    public void extractRenderState(GravityNormalizerBlockEntity entity, AnimatedBlockItemRenderer.State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.blockState = entity.getBlockState();
        state.animation = Mth.lerp(partialTick, entity.lastAnimation(), entity.animation());
    }

    @Override
    public void submit(AnimatedBlockItemRenderer.State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        AnimatedBlockItemRenderer.applySided(state.blockState, poseStack);
        submitPart(state.blockState, state.animation, poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public static void submitPart(BlockState state, float animation, PoseStack pose, SubmitNodeCollector collector, int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5, 0.7, 0.5);
        pose.mulPose(Axis.XP.rotationDegrees(animation));
        pose.mulPose(Axis.YP.rotationDegrees(animation));
        pose.mulPose(Axis.ZP.rotationDegrees(animation));
        float yRot = animation / 1.2f;
        pose.mulPose(Axis.YP.rotationDegrees(yRot));
        pose.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0, SIN_45));
        pose.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0, SIN_45));
        pose.mulPose(Axis.YP.rotationDegrees(yRot));
        pose.mulPose(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SIN_45, 0, SIN_45));
        pose.mulPose(Axis.YP.rotationDegrees(yRot));
        pose.translate(-0.5, -0.7, -0.5);
        ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(TOP), pose, collector, light, overlay);
        pose.popPose();

        for (int i = 0; i < 4; i++) {
            pose.pushPose();
            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(90 * i));
            pose.translate(-0.5, 0, -0.5);
            pose.translate(0.27, 0.27, 0.27);
            pose.mulPose(Axis.XP.rotationDegrees(Mth.sin(animation / 50 + i) * 10));
            pose.translate(-0.27, -0.27, -0.27);
            ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(TOE), pose, collector, light, overlay);
            pose.popPose();
        }
    }
}
