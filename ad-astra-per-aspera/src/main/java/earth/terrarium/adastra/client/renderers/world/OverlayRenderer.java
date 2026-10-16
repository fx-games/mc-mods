package earth.terrarium.adastra.client.renderers.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class OverlayRenderer {

    // Upstream GL state: translucent, no cull, no depth write, polygonOffset(-3, -3) (positive bias under reverse-Z).
    private static final RenderPipeline PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
        .withLocation("pipeline/adastra_area_overlay")
        .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 3.0F, 3.0F))
        .build());
    private static final RenderType RENDER_TYPE = RenderType.create("adastra_area_overlay",
        RenderSetup.builder(PIPELINE).sortOnUpload().createRenderSetup());

    private final Map<BlockPos, Set<BlockPos>> positions = new HashMap<>();
    private final int color;
    private final BooleanSupplier config;
    private final Supplier<Block> block;

    public OverlayRenderer(int color, BooleanSupplier config, Supplier<Block> block) {
        this.color = color;
        this.config = config;
        this.block = block;
    }

    public void addPositions(BlockPos pos, Set<BlockPos> positions) {
        this.positions.put(pos, positions);
    }

    public void removePositions(BlockPos pos) {
        positions.remove(pos);
    }

    public void clearPositions() {
        positions.clear();
    }

    public boolean canAdd(BlockPos pos) {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.blockPosition().closerThan(pos, 128);
    }

    public void render(PoseStack poseStack, SubmitNodeCollector collector, Vec3 camera) {
        if (!config.getAsBoolean()) return;

        var level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.getGameTime() % 40 == 0) {
            positions.keySet().removeIf(pos -> !level.isLoaded(pos)
                || !canAdd(pos)
                || !(level.getBlockState(pos).is(block.get()))
            );
        }
        if (positions.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(-camera.x(), -camera.y(), -camera.z());
        collector.submitCustomGeometry(poseStack, RENDER_TYPE, (pose, consumer) ->
            positions.values().forEach(set -> set.forEach(pos -> renderCube(pose.pose(), consumer, pos, set))));
        poseStack.popPose();
    }

    private void renderCube(Matrix4f matrix, VertexConsumer consumer, BlockPos pos, Set<BlockPos> others) {
        int minX = pos.getX();
        int minY = pos.getY();
        int minZ = pos.getZ();
        int maxX = minX + 1;
        int maxY = minY + 1;
        int maxZ = minZ + 1;

        // Bottom Face
        if (!others.contains(pos.below())) {
            consumer.addVertex(matrix, minX, minY, minZ).setColor(color);
            consumer.addVertex(matrix, maxX, minY, minZ).setColor(color);
            consumer.addVertex(matrix, maxX, minY, maxZ).setColor(color);
            consumer.addVertex(matrix, minX, minY, maxZ).setColor(color);
        }

        // Top Face
        if (!others.contains(pos.above())) {
            consumer.addVertex(matrix, minX, maxY, maxZ).setColor(color);
            consumer.addVertex(matrix, maxX, maxY, maxZ).setColor(color);
            consumer.addVertex(matrix, maxX, maxY, minZ).setColor(color);
            consumer.addVertex(matrix, minX, maxY, minZ).setColor(color);
        }

        // North Face
        if (!others.contains(pos.north())) {
            consumer.addVertex(matrix, minX, minY, minZ).setColor(color);
            consumer.addVertex(matrix, minX, maxY, minZ).setColor(color);
            consumer.addVertex(matrix, maxX, maxY, minZ).setColor(color);
            consumer.addVertex(matrix, maxX, minY, minZ).setColor(color);
        }

        // South Face
        if (!others.contains(pos.south())) {
            consumer.addVertex(matrix, maxX, minY, maxZ).setColor(color);
            consumer.addVertex(matrix, maxX, maxY, maxZ).setColor(color);
            consumer.addVertex(matrix, minX, maxY, maxZ).setColor(color);
            consumer.addVertex(matrix, minX, minY, maxZ).setColor(color);
        }

        // East Face
        if (!others.contains(pos.east())) {
            consumer.addVertex(matrix, maxX, minY, minZ).setColor(color);
            consumer.addVertex(matrix, maxX, maxY, minZ).setColor(color);
            consumer.addVertex(matrix, maxX, maxY, maxZ).setColor(color);
            consumer.addVertex(matrix, maxX, minY, maxZ).setColor(color);
        }

        // West Face
        if (!others.contains(pos.west())) {
            consumer.addVertex(matrix, minX, minY, maxZ).setColor(color);
            consumer.addVertex(matrix, minX, maxY, maxZ).setColor(color);
            consumer.addVertex(matrix, minX, maxY, minZ).setColor(color);
            consumer.addVertex(matrix, minX, minY, minZ).setColor(color);
        }
    }
}
