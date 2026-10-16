package earth.terrarium.adastra.client.renderers.ti69.apps;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.client.renderers.ti69.Ti69Renderer;
import earth.terrarium.adastra.client.utils.ClientData;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.constants.PlanetConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SensorApp implements Ti69App {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "sensor");

    @Override
    public void render(PoseStack pose, SubmitNodeCollector collector, ClientLevel level, boolean rightHanded) {
        PlanetData data = ClientData.getLocalData();
        if (data == null) return;
        this.renderTime(pose, collector, level);

        Component oxygen = data.oxygen() ? ConstantComponents.TRUE : ConstantComponents.FALSE;
        Component temperature = Component.translatable("text.ad_astra.temperature", data.temperature());
        Component gravity = Component.translatable("text.ad_astra.gravity", Math.round(data.gravity() * PlanetConstants.EARTH_GRAVITY * 1000) / 1000f);
        this.text(pose, collector, oxygen.getVisualOrderText(), 12, 17);
        this.text(pose, collector, temperature.getVisualOrderText(), 12, 30);
        this.text(pose, collector, gravity.getVisualOrderText(), 12, 43);

        this.renderIcon(pose, collector, Ti69Renderer.ICONS, 0, 17, 24, 0, 8, 8, 32, 32);
        this.renderIcon(pose, collector, Ti69Renderer.ICONS, 0, 30, 24, 8, 8, 8, 32, 32);
        this.renderIcon(pose, collector, Ti69Renderer.ICONS, 0, 43, 24, 16, 8, 8, 32, 32);
    }

    @Override
    public int color() {
        return 0xff3aabc3;
    }
}
