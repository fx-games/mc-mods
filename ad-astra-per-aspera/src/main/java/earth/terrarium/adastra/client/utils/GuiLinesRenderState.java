package earth.terrarium.adastra.client.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

/**
 * Replaces the old immediate-mode DEBUG_LINES drawing in GUIs: each segment becomes a 1px wide quad
 * submitted as a GUI render state.
 */
public record GuiLinesRenderState(
    Matrix3x2fc pose, float[] segments, int color,
    @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

    /** Collects segments (x1, y1, x2, y2) and submits them with {@link #submit}. */
    public static final class Builder {
        private final FloatArrayList segments = new FloatArrayList();

        public Builder line(float x1, float y1, float x2, float y2) {
            segments.add(x1);
            segments.add(y1);
            segments.add(x2);
            segments.add(y2);
            return this;
        }

        public void submit(GuiGraphicsExtractor graphics, int color) {
            if (segments.isEmpty()) return;
            ScreenRectangle scissor = graphics.scissorStack.peek();
            ScreenRectangle screen = new ScreenRectangle(0, 0, graphics.guiWidth(), graphics.guiHeight());
            graphics.guiRenderState.addGuiElement(new GuiLinesRenderState(
                new Matrix3x2f(graphics.pose()), segments.toFloatArray(), color,
                scissor, scissor != null ? scissor.intersection(screen) : screen));
        }
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        for (int i = 0; i + 3 < segments.length; i += 4) {
            float x1 = segments[i], y1 = segments[i + 1], x2 = segments[i + 2], y2 = segments[i + 3];
            float dx = x2 - x1, dy = y2 - y1;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len == 0) continue;
            float nx = -dy / len * 0.5f, ny = dx / len * 0.5f;
            consumer.addVertexWith2DPose(pose, x1 + nx, y1 + ny).setColor(color);
            consumer.addVertexWith2DPose(pose, x1 - nx, y1 - ny).setColor(color);
            consumer.addVertexWith2DPose(pose, x2 - nx, y2 - ny).setColor(color);
            consumer.addVertexWith2DPose(pose, x2 + nx, y2 + ny).setColor(color);
        }
    }

    @Override
    public RenderPipeline pipeline() {
        return RenderPipelines.GUI;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }
}
