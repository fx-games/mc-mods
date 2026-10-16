package earth.terrarium.adastra.client.renderers.entities.vehicles;

import com.mojang.serialization.MapCodec;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.models.entities.vehicles.RoverModel;
import earth.terrarium.adastra.common.entities.vehicles.Rover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RoverRenderer extends VehicleRenderer<Rover> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/rover/tier_1_rover.png");

    public RoverRenderer(EntityRendererProvider.Context context) {
        super(context, new RoverModel(context.bakeLayer(RoverModel.LAYER)), TEXTURE, 1.0f);
    }

    @Override
    public void extractRenderState(Rover entity, VehicleRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (!Minecraft.getInstance().isPaused()) {
            entity.wheelXRot += entity.speed() / 7.5f;
            entity.wheelYRot = Mth.clamp(entity.angle() / 10, -0.3f, 0.3f) * Math.signum(entity.speed());
        }
        state.wheelXRot = entity.wheelXRot;
        state.wheelYRot = entity.wheelYRot;
    }

    /**
     * "ad_astra:rover" special item model type.
     */
    public record ItemUnbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<ItemUnbaked> MAP_CODEC = MapCodec.unit(new ItemUnbaked());

        @Override
        public MapCodec<ItemUnbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public RocketRenderer.ItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new RocketRenderer.ItemRenderer(new RoverModel(context.entityModelSet().bakeLayer(RoverModel.LAYER)), TEXTURE);
        }
    }
}
