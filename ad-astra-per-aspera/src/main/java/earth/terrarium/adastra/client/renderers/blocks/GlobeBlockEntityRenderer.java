package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.blockentities.GlobeBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GlobeBlockEntityRenderer implements BlockEntityRenderer<GlobeBlockEntity, AnimatedBlockItemRenderer.State> {

    public GlobeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public static Identifier cubeModel(BlockState state) {
        return Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/%s_cube".formatted(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()));
    }

    @Override
    public AnimatedBlockItemRenderer.State createRenderState() {
        return new AnimatedBlockItemRenderer.State();
    }

    @Override
    public void extractRenderState(GlobeBlockEntity entity, AnimatedBlockItemRenderer.State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.blockState = entity.getBlockState();
        state.animation = Mth.lerp(partialTick, entity.lastYRot(), entity.yRot());
    }

    @Override
    public void submit(AnimatedBlockItemRenderer.State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submitPart(state.blockState, state.animation, poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY);
    }

    public static void submitPart(BlockState state, float yRot, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5, 0, -0.5);
        ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(cubeModel(state)), poseStack, collector, light, overlay);
        poseStack.popPose();
    }
}
