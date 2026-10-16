package earth.terrarium.adastra.client.renderers.entities.mobs;

import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.models.entities.mobs.PygroModel;
import earth.terrarium.adastra.client.models.entities.mobs.PygroModel;
import earth.terrarium.adastra.common.entities.mob.Pygro;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class PygroRenderer extends HumanoidMobRenderer<Pygro, HumanoidRenderState, PygroModel> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/mob/pygro.png");

    public PygroRenderer(EntityRendererProvider.Context context) {
        super(context, new PygroModel(context.bakeLayer(PygroModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this, ArmorModelSet.bake(ModelLayers.PIGLIN_ARMOR, context.getModelSet(), HumanoidModel::new), context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return TEXTURE;
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }
}
