package earth.terrarium.adastra.client.renderers.entities.vehicles;

import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.models.entities.vehicles.LanderModel;
import earth.terrarium.adastra.common.entities.vehicles.Lander;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class LanderRenderer extends VehicleRenderer<Lander> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/entity/lander/lander.png");

    public LanderRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context, new LanderModel(context.bakeLayer(layer)), TEXTURE, 0.5f);
    }
}
