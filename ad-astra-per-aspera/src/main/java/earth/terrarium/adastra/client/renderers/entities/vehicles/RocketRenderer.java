package earth.terrarium.adastra.client.renderers.entities.vehicles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import com.mojang.serialization.MapCodec;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.models.entities.vehicles.RocketModel;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class RocketRenderer extends VehicleRenderer<Rocket> {

    public static final Identifier TIER_1_TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/rocket/tier_1_rocket.png");
    public static final Identifier TIER_2_TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/rocket/tier_2_rocket.png");
    public static final Identifier TIER_3_TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/rocket/tier_3_rocket.png");
    public static final Identifier TIER_4_TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/rocket/tier_4_rocket.png");

    public RocketRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, Identifier texture) {
        super(context, new RocketModel(context.bakeLayer(layer)), texture, 0.5f);
    }

    @Override
    public void extractRenderState(Rocket entity, VehicleRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        // Shake rocket constantly
        if (!Minecraft.getInstance().isPaused() && (entity.isLaunching() || entity.hasLaunched())) {
            state.yRot += (float) (entity.level().getRandom().nextGaussian() * 0.3);
        }
    }

    /**
     * Item renderer, used by the "ad_astra:rocket" special item model type.
     */
    public static class ItemRenderer implements NoDataSpecialModelRenderer {

        private static final VehicleRenderState STATE = new VehicleRenderState();
        private final EntityModel<VehicleRenderState> model;
        private final Identifier texture;

        public ItemRenderer(EntityModel<VehicleRenderState> model, Identifier texture) {
            this.model = model;
            this.texture = texture;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(0.0, -1.501, 0.0);
            collector.submitModel(model, STATE, poseStack, RenderTypes.entityCutoutZOffset(texture), light, overlay, outlineColor, null);
            poseStack.popPose();
        }

        @Override
        public void getExtents(Consumer<Vector3fc> output) {
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.translate(0.0, -1.501, 0.0);
            model.root().getExtentsForGui(poseStack, output);
        }

        public record Unbaked(int tier) implements NoDataSpecialModelRenderer.Unbaked {
            /** One unit codec per tier, registered as ad_astra:tier_N_rocket. */
            public static final List<MapCodec<Unbaked>> CODECS = List.of(
                MapCodec.unit(new Unbaked(1)), MapCodec.unit(new Unbaked(2)),
                MapCodec.unit(new Unbaked(3)), MapCodec.unit(new Unbaked(4)));

            @Override
            public MapCodec<Unbaked> type() {
                return CODECS.get(tier - 1);
            }

            @Override
            public ItemRenderer bake(SpecialModelRenderer.BakingContext context) {
                ModelLayerLocation layer = switch (tier) {
                    case 1 -> RocketModel.TIER_1_LAYER;
                    case 2 -> RocketModel.TIER_2_LAYER;
                    case 3 -> RocketModel.TIER_3_LAYER;
                    default -> RocketModel.TIER_4_LAYER;
                };
                Identifier texture = switch (tier) {
                    case 1 -> TIER_1_TEXTURE;
                    case 2 -> TIER_2_TEXTURE;
                    case 3 -> TIER_3_TEXTURE;
                    default -> TIER_4_TEXTURE;
                };
                return new ItemRenderer(new RocketModel(context.entityModelSet().bakeLayer(layer)), texture);
            }
        }
    }
}
