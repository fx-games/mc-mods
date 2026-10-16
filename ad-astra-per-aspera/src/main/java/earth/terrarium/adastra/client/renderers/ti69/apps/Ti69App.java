package earth.terrarium.adastra.client.renderers.ti69.apps;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;

public interface Ti69App {

    /**
     * Renders the contents of the app
     *
     * @param pose        The pose stack
     * @param collector   The submit node collector
     * @param level       The client level
     * @param rightHanded Whether the player is right-handed
     */
    void render(PoseStack pose, SubmitNodeCollector collector, ClientLevel level, boolean rightHanded);

    /**
     * @return The background color of the app
     */
    int color();

    default void text(PoseStack pose, SubmitNodeCollector collector, FormattedCharSequence text, float x, float y) {
        collector.submitText(pose, x, y, text, false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, 0xFFFFFFFF, 0, 0);
    }

    /**
     * Renders an icon
     */
    default void renderIcon(PoseStack pose, SubmitNodeCollector collector, Identifier icon, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        int x2 = x + uWidth;
        int y2 = y + vHeight;
        float minU = (float) uOffset / (float) textureWidth;
        float maxU = ((float) uOffset + (float) uWidth) / (float) textureWidth;
        float minV = (float) vOffset / (float) textureHeight;
        float maxV = ((float) vOffset + (float) vHeight) / (float) textureHeight;
        int light = LightCoordsUtil.FULL_BRIGHT;

        collector.submitCustomGeometry(pose, RenderTypes.text(icon), (last, consumer) -> {
            var m = last.pose();
            consumer.addVertex(m, x, y, 0).setColor(-1).setUv(minU, minV).setLight(light);
            consumer.addVertex(m, x, y2, 0).setColor(-1).setUv(minU, maxV).setLight(light);
            consumer.addVertex(m, x2, y2, 0).setColor(-1).setUv(maxU, maxV).setLight(light);
            consumer.addVertex(m, x2, y, 0).setColor(-1).setUv(maxU, minV).setLight(light);
        });
    }

    /**
     * Renders the time
     */
    default void renderTime(PoseStack pose, SubmitNodeCollector collector, ClientLevel level) {
        double ratio = 1000.0 / 60.0;
        long time = level.getDefaultClockTime();
        int dayTime = (int) ((time + 6000L) % 12000L);
        boolean isPm = (int) ((time + 6000L) % 24000L) >= 12000;
        int hours = dayTime / 1000 == 0 ? 12 : dayTime / 1000;
        int minutes = (int) ((dayTime % 1000) / ratio);
        String timeText = hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + (isPm ? " PM" : " AM");
        text(pose, collector, FormattedCharSequence.forward(timeText, Style.EMPTY), 0.0f, 5.0f);
    }
}
