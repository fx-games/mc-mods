package earth.terrarium.adastra.client.dimension;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.client.utils.DimensionRenderingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Replaces vanilla's sun, moon and stars with the planet renderer's sky renderables and star field.
 * Called from SkyRendererMixin; the sky disc, sunrise fan and dark disc stay vanilla (driven by environment attributes).
 */
public final class ModSkyRenderer {

    // Vanilla STARS with a per-vertex colour, for the planet's star_colors.
    private static final RenderPipeline COLORED_STARS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
        .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
        .withLocation("pipeline/adastra_colored_stars")
        .withVertexShader("core/position_color")
        .withFragmentShader("core/position_color")
        .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .build());

    // Vanilla CELESTIAL with normal alpha blending instead of additive, for renderables with blend: false.
    private static final RenderPipeline OPAQUE_CELESTIAL = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
        .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
        .withLocation("pipeline/adastra_opaque_celestial")
        .withVertexShader("core/position_tex")
        .withFragmentShader("core/position_tex")
        .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .build());

    // ponytail: buffers live for the whole session (stars rebuilt when the planet renderer changes), close them on reload if it matters
    @Nullable
    private static GpuBuffer quadBuffer;
    @Nullable
    private static GpuBuffer starBuffer;
    @Nullable
    private static PlanetRenderer starRenderer;
    private static int starIndexCount;

    /**
     * @return true if the sky was rendered and vanilla's sun/moon/stars should be skipped
     */
    @Nullable
    public static PlanetRenderer current() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        PlanetRenderer renderer = AdAstraPlanetRenderers.get(level.dimension());
        return renderer != null && renderer.customSky() ? renderer : null;
    }

    /**
     * Replaces the timeline's sunrise colour with the planet's (the old ModDimensionSpecialEffects formula).
     */
    public static void extractSunrise(SkyRenderState state) {
        PlanetRenderer renderer = current();
        if (renderer == null) return;
        int sunColor = renderer.sunriseColor();
        float timeCos = Mth.cos(state.sunAngle);
        if (timeCos < -0.4f || timeCos > 0.4f) {
            state.sunriseAndSunsetColor = 0;
            return;
        }
        float time = timeCos / 0.4f * 0.5f + 0.5f;
        float alpha = 1 - (1 - Mth.sin(time * (float) Math.PI)) * 0.99f;
        state.sunriseAndSunsetColor = ARGB.colorFromFloat(alpha * alpha,
            Mth.clamp(time * 0.3f + ARGB.red(sunColor) / 255f * 0.7f, 0, 1),
            Mth.clamp(time * time * 0.7f + ARGB.green(sunColor) / 255f * 0.5f, 0, 1),
            ARGB.blue(sunColor) / 255f * 0.6f);
    }

    public static float sunriseAngle() {
        PlanetRenderer renderer = current();
        return renderer == null ? 0 : renderer.sunriseAngle();
    }

    public static boolean render(PoseStack poseStack, float sunAngle, float rainBrightness, float starBrightness) {
        PlanetRenderer renderer = current();
        if (renderer == null) return false;
        float alpha = renderer.renderInRain() ? 1 : rainBrightness;

        float stars = renderer.starBrightness().orElse(starBrightness * rainBrightness);
        if (stars > 0 && renderer.stars() > 0) renderStars(poseStack, renderer, stars);

        float timeOfDay = sunAngle * Mth.RAD_TO_DEG;
        for (SkyRenderable renderable : renderer.skyRenderables()) {
            Vec3 globalRotation = switch (renderable.movementType()) {
                case STATIC -> renderable.globalRotation();
                case TIME_OF_DAY -> renderable.globalRotation().add(0, 0, timeOfDay);
                case TIME_OF_DAY_REVERSED -> renderable.globalRotation().add(0, 0, -timeOfDay);
            };
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees((float) globalRotation.x));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) globalRotation.y));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) globalRotation.z));
            poseStack.translate(0, 100, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) renderable.localRotation().x));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) renderable.localRotation().y));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) renderable.localRotation().z));
            renderQuad(poseStack, renderable.blend() ? RenderPipelines.CELESTIAL : OPAQUE_CELESTIAL, renderable.texture(), renderable.scale(), new Vector4f(1, 1, 1, alpha));
            if (renderable.backLightScale() > 0) {
                int color = renderable.backLightColor();
                renderQuad(poseStack, RenderPipelines.CELESTIAL, DimensionRenderingUtils.BACKLIGHT, renderable.backLightScale(),
                    new Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), alpha));
            }
            poseStack.popPose();
        }
        return true;
    }

    private static void renderQuad(PoseStack poseStack, RenderPipeline pipeline, Identifier texture, float scale, Vector4f color) {
        AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(texture);
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(poseStack.last().pose());
        modelViewStack.scale(scale, 1, scale);
        draw("Ad Astra sky renderable", pipeline, quad(), 6, new Matrix4f(modelViewStack), color, tex);
        modelViewStack.popMatrix();
    }

    private static void renderStars(PoseStack poseStack, PlanetRenderer renderer, float brightness) {
        if (starRenderer != renderer) buildStars(renderer);
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(poseStack.last().pose());
        draw("Ad Astra stars", COLORED_STARS, starBuffer, starIndexCount, new Matrix4f(modelViewStack),
            new Vector4f(brightness, brightness, brightness, brightness), null);
        modelViewStack.popMatrix();
    }

    private static void draw(String name, RenderPipeline pipeline, GpuBuffer vertices, int indexCount, Matrix4f pose, Vector4f color, @Nullable AbstractTexture texture) {
        RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
        GpuBuffer indexBuffer = indices.getBuffer(indexCount);
        RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = target.getColorTextureView();
        GpuTextureView depthTexture = target.getDepthTextureView();
        var transforms = RenderSystem.getDynamicUniforms().writeTransform(pose, color);
        try (RenderPass renderPass = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(() -> name, colorTexture, Optional.empty(), depthTexture, OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);
            if (texture != null) renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
            renderPass.setVertexBuffer(0, vertices.slice());
            renderPass.setIndexBuffer(indexBuffer, indices.type());
            renderPass.drawIndexed(indexCount, 1, 0, 0, 0);
        }
    }

    private static GpuBuffer quad() {
        if (quadBuffer != null) return quadBuffer;
        try (ByteBufferBuilder builder = ByteBufferBuilder.exactlySized(4 * DefaultVertexFormat.POSITION_TEX.getVertexSize())) {
            BufferBuilder buffer = new BufferBuilder(builder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.addVertex(-1, 0, -1).setUv(1, 0);
            buffer.addVertex(1, 0, -1).setUv(0, 0);
            buffer.addVertex(1, 0, 1).setUv(0, 1);
            buffer.addVertex(-1, 0, 1).setUv(1, 1);
            try (MeshData mesh = buffer.buildOrThrow()) {
                return quadBuffer = RenderSystem.getDevice().createBuffer(() -> "Ad Astra sky quad", 32, mesh.vertexBuffer());
            }
        }
    }

    // Same layout as vanilla's star field, with the planet's star count and colours.
    private static void buildStars(PlanetRenderer renderer) {
        int count = renderer.stars();
        if (starBuffer != null) starBuffer.close();
        RandomSource random = RandomSource.create(10842L);
        try (ByteBufferBuilder builder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR.getVertexSize() * count * 4)) {
            BufferBuilder buffer = new BufferBuilder(builder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (int i = 0; i < count; i++) {
                float x = random.nextFloat() * 2 - 1;
                float y = random.nextFloat() * 2 - 1;
                float z = random.nextFloat() * 2 - 1;
                float size = 0.15f + random.nextFloat() * 0.1f;
                float lengthSq = Mth.lengthSquared(x, y, z);
                if (lengthSq <= 0.01f || lengthSq >= 1) continue;
                Vector3f center = new Vector3f(x, y, z).normalize(100);
                float zRot = (float) (random.nextDouble() * Math.PI * 2);
                int color = renderer.starColors().getRandom(random).orElse(0xffffffff);
                Matrix3f rotation = new Matrix3f().rotateTowards(new Vector3f(center).negate(), new Vector3f(0, 1, 0)).rotateZ(-zRot);
                buffer.addVertex(new Vector3f(size, -size, 0).mul(rotation).add(center)).setColor(color);
                buffer.addVertex(new Vector3f(size, size, 0).mul(rotation).add(center)).setColor(color);
                buffer.addVertex(new Vector3f(-size, size, 0).mul(rotation).add(center)).setColor(color);
                buffer.addVertex(new Vector3f(-size, -size, 0).mul(rotation).add(center)).setColor(color);
            }
            try (MeshData mesh = buffer.buildOrThrow()) {
                starIndexCount = mesh.drawState().indexCount();
                starBuffer = RenderSystem.getDevice().createBuffer(() -> "Ad Astra stars", 40, mesh.vertexBuffer());
                starRenderer = renderer;
            }
        }
    }
}
