package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.blockentities.SlidingDoorBlockEntity;
import earth.terrarium.adastra.common.blocks.SlidingDoorBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SlidingDoorBlockEntityRenderer implements BlockEntityRenderer<SlidingDoorBlockEntity, AnimatedBlockItemRenderer.State> {

    public SlidingDoorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public AnimatedBlockItemRenderer.State createRenderState() {
        return new AnimatedBlockItemRenderer.State();
    }

    @Override
    public void extractRenderState(SlidingDoorBlockEntity entity, AnimatedBlockItemRenderer.State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.blockState = entity.getBlockState();
        state.animation = Mth.lerp(partialTick, entity.lastSlideTicks(), entity.slideTicks()) / 81.0f;
    }

    @Override
    public void submit(AnimatedBlockItemRenderer.State renderState, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        float slide = renderState.animation;
        var state = renderState.blockState;
        int light = renderState.lightCoords;
        var direction = state.getValue(SlidingDoorBlock.FACING);
        var model = ClientPlatformUtils.getModel(state);
        boolean flipSecondDoor = ModBlocks.SIMPLE_SLIDING_DOORS.stream().noneMatch(block -> block.get().equals(state.getBlock()));

        pose.pushPose();
        pose.translate(0.5f, 1, 0.5f);
        pose.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));
        pose.translate(-0.5f, 0, -0.5f);

        pose.translate(slide, 0, 0.0625f);
        if (direction.getAxis() == Direction.Axis.Z) {
            pose.translate(0, 0, 0.6875f);
            if (state.is(ModBlocks.REINFORCED_DOOR.get())) {
                pose.translate(0, 0, -0.3125f);
            }
        }

        ClientPlatformUtils.submitBlockModel(model, pose, collector, light);

        pose.translate(-slide - slide, 0, 0);

        if (!flipSecondDoor) {
            pose.translate(0.5f, 0, 0.5f);
            pose.mulPose(Axis.YP.rotationDegrees(180));
            pose.translate(-0.5f, 0, -0.5f);
            pose.translate(0, 0, 0.8125f);
            ClientPlatformUtils.submitBlockModel(model, pose, collector, light);
        } else {
            pose.translate(-1.25f, 0, 0);
            ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(flippedModel(state.getBlock())), pose, collector, light);
        }
        pose.popPose();
    }

    public static Identifier flippedModel(net.minecraft.world.level.block.Block block) {
        return Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/%s_flipped".formatted(BuiltInRegistries.BLOCK.getKey(block).getPath()));
    }
}
