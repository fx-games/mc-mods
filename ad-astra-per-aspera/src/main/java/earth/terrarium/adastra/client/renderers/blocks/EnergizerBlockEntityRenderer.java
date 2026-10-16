package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.common.blockentities.machines.EnergizerBlockEntity;
import earth.terrarium.adastra.common.blocks.base.MachineBlock;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EnergizerBlockEntityRenderer implements BlockEntityRenderer<EnergizerBlockEntity, EnergizerBlockEntityRenderer.State> {

    private final ItemModelResolver itemModelResolver;

    public EnergizerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float time;
        public int lightAbove;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EnergizerBlockEntity entity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.item.clear();
        var stack = entity.getItem(0);
        if (stack.isEmpty() || entity.getLevel() == null) return;
        if (!entity.getBlockState().getValue(MachineBlock.LIT)) return;
        itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.GROUND, entity.getLevel(), null, 0);
        state.time = entity.getLevel().getGameTime() + partialTick;
        state.lightAbove = LightCoordsUtil.getLightCoords(entity.getLevel(), entity.getBlockPos().above());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.item.isEmpty()) return;
        poseStack.pushPose();
        double offset = Math.sin(state.time / 8.0) / 8.0;
        poseStack.translate(0.5, 1.6 + offset, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.time * 4));
        state.item.submit(poseStack, collector, state.lightAbove, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
