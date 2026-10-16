package earth.terrarium.adastra.client.renderers.entities.mobs;

import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.models.entities.mobs.GlacianRamModel;
import earth.terrarium.adastra.common.entities.mob.GlacianRam;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.Identifier;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class GlacianRamRenderer extends MobRenderer<GlacianRam, SheepRenderState, GlacianRamModel> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/mob/glacian_ram/glacian_ram.png");
    private static final Identifier SHEARED_TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/mob/glacian_ram/sheared_glacian_ram.png");

    public GlacianRamRenderer(EntityRendererProvider.Context context) {
        super(context, new GlacianRamModel(context.bakeLayer(GlacianRamModel.LAYER_LOCATION)), 0.7f);
    }

    @Override
    public Identifier getTextureLocation(SheepRenderState state) {
        return state.isSheared ? SHEARED_TEXTURE : TEXTURE;
    }

    @Override
    public SheepRenderState createRenderState() {
        return new SheepRenderState();
    }

    @Override
    public void extractRenderState(GlacianRam entity, SheepRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSheared = entity.isSheared();
        state.headEatPositionScale = entity.getNeckAngle(partialTicks);
        state.headEatAngleScale = entity.getHeadAngle(partialTicks);
    }
}
