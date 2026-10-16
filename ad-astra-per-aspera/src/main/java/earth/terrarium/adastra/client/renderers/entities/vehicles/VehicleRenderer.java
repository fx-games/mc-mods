package earth.terrarium.adastra.client.renderers.entities.vehicles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * Shared renderer for rockets, rovers and landers.
 */
public abstract class VehicleRenderer<T extends Entity> extends EntityRenderer<T, VehicleRenderState> {

    protected final EntityModel<VehicleRenderState> model;
    protected final Identifier texture;

    protected VehicleRenderer(EntityRendererProvider.Context context, EntityModel<VehicleRenderState> model, Identifier texture, float shadowRadius) {
        super(context);
        this.shadowRadius = shadowRadius;
        this.model = model;
        this.texture = texture;
    }

    @Override
    public VehicleRenderState createRenderState() {
        return new VehicleRenderState();
    }

    @Override
    public void extractRenderState(T entity, VehicleRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        state.xRot = entity.getXRot(partialTicks);
    }

    @Override
    public void submit(VehicleRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.55F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-state.xRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(model, state, poseStack, model.renderType(texture), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
    }
}
