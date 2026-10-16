package dev.explorercraft.starlight.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import dev.explorercraft.starlight.SkyCatalog;
import dev.explorercraft.starlight.SkyClock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.OptionalDouble;

/// Draws the catalogue in place of vanilla's random stars: square blocky stars like vanilla's,
/// sized by brightness, and the stick figures that fade in behind a spyglass.
///
/// Everything is built once in the equatorial frame and turned into place each frame by a
/// single matrix, so the sky costs the same as vanilla's.
public final class StarField implements AutoCloseable {
    private static final float RADIUS = 100;
    private static final float LINE_HALF_WIDTH = 0.06f;
    /// How much of the figure lines shows at full spyglass fade.
    private static final float OVERLAY_STRENGTH = 0.4f;
    private static final float BACKGROUND_DIM = 0.65f;

    /// What the HUD label needs from the last frame drawn.
    public static Matrix4f lastEquatorialToWorld = new Matrix4f();
    public static double lastSidereal;
    public static float lastStarBrightness;
    /// 0 without a spyglass, 1 looking through one; eased in between.
    public static float overlay;

    private final GpuBuffer starBuffer;
    private final int figureIndexCount;
    private final int starIndexCount;
    private final GpuBuffer lineBuffer;
    private final int lineIndexCount;
    private final RenderSystem.AutoStorageIndexBuffer quadIndices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
    private long lastFrameNanos = System.nanoTime();

    public StarField() {
        var sky = SkyCatalog.INSTANCE;

        int quads = 0, figureQuads = 0;
        for (var star : sky.stars) {
            int n = star.kind() == SkyCatalog.Kind.POLARIS ? 5 : 1;
            quads += n;
            if (star.kind() != SkyCatalog.Kind.BACKGROUND) figureQuads += n;
        }
        try (var bytes = ByteBufferBuilder.exactlySized(quads * 4 * DefaultVertexFormat.POSITION.getVertexSize())) {
            var builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION);
            // figure stars first, so one draw call can light them and another the dimmer rest
            for (var kind : SkyCatalog.Kind.values()) {
                if (kind == SkyCatalog.Kind.BACKGROUND) continue;
                for (int i = 0; i < sky.stars.size(); i++) if (sky.stars.get(i).kind() == kind) addStar(builder, sky.stars.get(i), i);
            }
            for (int i = 0; i < sky.stars.size(); i++) if (sky.stars.get(i).kind() == SkyCatalog.Kind.BACKGROUND) addStar(builder, sky.stars.get(i), i);
            try (MeshData mesh = builder.buildOrThrow()) {
                starIndexCount = mesh.drawState().indexCount();
                figureIndexCount = figureQuads * 6;
                starBuffer = RenderSystem.getDevice().createBuffer(() -> "Starlight stars", 40, mesh.vertexBuffer());
            }
        }

        int segments = sky.figures.stream().mapToInt(f -> f.lines().length / 2).sum();
        try (var bytes = ByteBufferBuilder.exactlySized(segments * 8 * DefaultVertexFormat.POSITION.getVertexSize())) {
            var builder = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION);
            for (var figure : sky.figures) {
                int[] lines = figure.lines();
                for (int i = 0; i < lines.length; i += 2) addLine(builder, sky.stars.get(lines[i]), sky.stars.get(lines[i + 1]));
            }
            MeshData mesh = builder.build();
            if (mesh == null) {
                lineBuffer = null;
                lineIndexCount = 0;
            } else {
                try (mesh) {
                    lineIndexCount = mesh.drawState().indexCount();
                    lineBuffer = RenderSystem.getDevice().createBuffer(() -> "Starlight figures", 40, mesh.vertexBuffer());
                }
            }
        }
    }

    /// Half the edge of a star's square. Vanilla's are 0.15 to 0.25; figure stars sit clearly
    /// above that and background stars below, so the constellations are what the eye finds.
    static float size(SkyCatalog.Star star) {
        return switch (star.kind()) {
            case POLARIS -> 0.42f;
            case FIGURE -> Math.clamp(0.42f - 0.05f * star.mag(), 0.22f, 0.5f);
            case BACKGROUND -> Math.clamp(0.16f - 0.012f * star.mag(), 0.09f, 0.16f);
        };
    }

    private static Vector3f centre(SkyCatalog.Star star) {
        float[] d = star.direction();
        return new Vector3f(d[0], d[1], d[2]).mul(RADIUS);
    }

    private static void addStar(BufferBuilder builder, SkyCatalog.Star star, int index) {
        Vector3f centre = centre(star);
        float s = size(star);
        Vector3f up = Math.abs(centre.z) > 0.9f * RADIUS ? new Vector3f(1, 0, 0) : new Vector3f(0, 0, 1);
        // vanilla tilts every star by a random angle; a fixed one per star keeps that look
        float tilt = star.kind() == SkyCatalog.Kind.POLARIS ? 0 : (index * 0.618034f % 1f) * (float) Math.PI * 2;
        Matrix3f rotation = new Matrix3f().rotateTowards(new Vector3f(centre).negate(), up).rotateZ(-tilt);

        quad(builder, rotation, centre, 0, 0, s);
        if (star.kind() == SkyCatalog.Kind.POLARIS) {
            // a pixel sparkle: four small squares around the centre, unmistakable among squares
            float arm = s * 0.5f, offset = s + arm;
            quad(builder, rotation, centre, offset, 0, arm);
            quad(builder, rotation, centre, -offset, 0, arm);
            quad(builder, rotation, centre, 0, offset, arm);
            quad(builder, rotation, centre, 0, -offset, arm);
        }
    }

    private static void quad(BufferBuilder builder, Matrix3f rotation, Vector3f centre, float dx, float dy, float s) {
        builder.addVertex(new Vector3f(dx + s, dy - s, 0).mul(rotation).add(centre));
        builder.addVertex(new Vector3f(dx + s, dy + s, 0).mul(rotation).add(centre));
        builder.addVertex(new Vector3f(dx - s, dy + s, 0).mul(rotation).add(centre));
        builder.addVertex(new Vector3f(dx - s, dy - s, 0).mul(rotation).add(centre));
    }

    /// A thin flat band from star to star, stopping short of both so the stars stay crisp.
    private static void addLine(BufferBuilder builder, SkyCatalog.Star from, SkyCatalog.Star to) {
        Vector3f a = centre(from), b = centre(to);
        Vector3f dir = new Vector3f(b).sub(a);
        float length = dir.length();
        float gapA = size(from) * 1.6f + 0.15f, gapB = size(to) * 1.6f + 0.15f;
        if (length <= gapA + gapB) return;
        dir.div(length);
        Vector3f side = new Vector3f(dir).cross(new Vector3f(a).add(b).normalize()).normalize(LINE_HALF_WIDTH);
        Vector3f start = new Vector3f(dir).mul(gapA).add(a), end = new Vector3f(dir).mul(-gapB).add(b);

        Vector3f p0 = new Vector3f(start).sub(side), p1 = new Vector3f(start).add(side);
        Vector3f p2 = new Vector3f(end).add(side), p3 = new Vector3f(end).sub(side);
        // both windings, so culling cannot hide it whichever way the band faces
        builder.addVertex(p0).addVertex(p1).addVertex(p2).addVertex(p3);
        builder.addVertex(p3).addVertex(p2).addVertex(p1).addVertex(p0);
    }

    public void render(Matrix4f pose, float starAngle, float starBrightness, RenderTarget target) {
        var minecraft = Minecraft.getInstance();
        long now = System.nanoTime();
        float seconds = Math.min((now - lastFrameNanos) / 1e9f, 0.25f);
        lastFrameNanos = now;
        boolean scoping = minecraft.player != null && minecraft.player.isScoping();
        // about half a second to fade in, a little quicker out
        overlay = Math.clamp(overlay + (scoping ? seconds * 2f : -seconds * 3f), 0f, 1f);

        double sidereal = SkyClock.localSiderealDegrees(SkyClock.date(), SkyClock.solarHours(starAngle));
        Matrix4f toWorld = SkyClock.equatorialToWorld(sidereal);
        lastEquatorialToWorld = toWorld;
        lastSidereal = sidereal;
        lastStarBrightness = starBrightness;

        var modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.mul(pose).mul(toWorld);
        Matrix4f transform = new Matrix4f(modelView);
        modelView.popMatrix();

        float dim = starBrightness * BACKGROUND_DIM;
        draw(target, "Starlight stars", starBuffer, transform, new Vector4f(starBrightness), 0, figureIndexCount);
        draw(target, "Starlight background", starBuffer, transform, new Vector4f(dim), figureIndexCount, starIndexCount - figureIndexCount);

        float line = overlay * OVERLAY_STRENGTH * Math.min(1f, starBrightness * 2f);
        if (lineBuffer != null && line > 0.003f) {
            // a cool blue, so the figures read as an overlay rather than more stars
            draw(target, "Starlight figures", lineBuffer, transform, new Vector4f(0.6f * line, 0.8f * line, line, line), 0, lineIndexCount);
        }
    }

    private void draw(RenderTarget target, String name, GpuBuffer vertices, Matrix4f transform, Vector4f colour, int firstIndex, int indexCount) {
        if (indexCount <= 0) return;
        var transforms = RenderSystem.getDynamicUniforms().writeTransform(transform, colour);
        GpuBuffer indices = quadIndices.getBuffer(firstIndex + indexCount);
        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> name,
                target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            pass.setPipeline(RenderPipelines.STARS);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", transforms);
            pass.setVertexBuffer(0, vertices.slice());
            pass.setIndexBuffer(indices, quadIndices.type());
            pass.drawIndexed(indexCount, 1, firstIndex, 0, 0);
        }
    }

    @Override
    public void close() {
        starBuffer.close();
        if (lineBuffer != null) lineBuffer.close();
    }
}
