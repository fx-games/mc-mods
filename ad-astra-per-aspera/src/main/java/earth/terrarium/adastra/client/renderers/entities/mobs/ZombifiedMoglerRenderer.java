package earth.terrarium.adastra.client.renderers.entities.mobs;

import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.models.entities.mobs.MoglerModel;
import earth.terrarium.adastra.common.entities.mob.ZombifiedMogler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HoglinRenderState;
import net.minecraft.resources.Identifier;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class ZombifiedMoglerRenderer extends MobRenderer<ZombifiedMogler, HoglinRenderState, MoglerModel> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/mob/zombified_mogler.png");

    public ZombifiedMoglerRenderer(EntityRendererProvider.Context context) {
        super(context, new MoglerModel(context.bakeLayer(MoglerModel.LAYER_LOCATION)), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(HoglinRenderState state) {
        return TEXTURE;
    }

    @Override
    public HoglinRenderState createRenderState() {
        return new HoglinRenderState();
    }

    @Override
    public void extractRenderState(ZombifiedMogler entity, HoglinRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.attackAnimationRemainingTicks = entity.getAttackAnimationRemainingTicks();
    }
}
