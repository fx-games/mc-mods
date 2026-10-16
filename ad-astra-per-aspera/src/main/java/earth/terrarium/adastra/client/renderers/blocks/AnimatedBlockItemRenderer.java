package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Item renderer for blocks with an animated block entity part (globes, oxygen distributor, gravity normalizer).
 * Renders the block model plus the animated part.
 */
public record AnimatedBlockItemRenderer(BlockState state, float millisDivisor, float modulo, Part part) implements NoDataSpecialModelRenderer {

    /** Shared block entity render state: the block state and the interpolated animation value. */
    public static class State extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState {
        public BlockState blockState = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        public float animation;
    }

    /** Applies the floor/wall/ceiling transform of a sided machine block. */
    public static void applySided(BlockState state, PoseStack pose) {
        var face = state.getValue(earth.terrarium.adastra.common.blocks.base.SidedMachineBlock.FACE);
        var direction = state.getValue(earth.terrarium.adastra.common.blocks.base.SidedMachineBlock.FACING);
        if (face == net.minecraft.world.level.block.state.properties.AttachFace.CEILING) {
            pose.translate(0, 1, 1);
            pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));
        } else if (face == net.minecraft.world.level.block.state.properties.AttachFace.WALL) {
            switch (direction) {
                case NORTH -> pose.translate(0, 0, 1);
                case SOUTH -> pose.translate(1, 0, 0);
                case WEST -> pose.translate(1, 0, 1);
                default -> {}
            }
            switch (direction) { // Taken from geckolib
                case SOUTH -> pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
                case WEST -> pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
                case EAST -> pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270));
                default -> {}
            }
            pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90));
        }
    }

    @FunctionalInterface
    public interface Part {
        void submit(BlockState state, float animation, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor) {
        float animation = Util.getMillis() / millisDivisor;
        if (modulo > 0) animation %= modulo;
        ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(state), poseStack, collector, light, overlay);
        part.submit(state, animation, poseStack, collector, light, overlay);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0));
        output.accept(new Vector3f(1, 1, 1));
    }

    public record Unbaked(MapCodec<Unbaked> codec, Supplier<? extends Block> block, float millisDivisor, float modulo, Part part) implements NoDataSpecialModelRenderer.Unbaked {

        /**
         * Creates a codec for an item model type that always bakes to the given renderer.
         */
        public static MapCodec<Unbaked> codec(Supplier<? extends Block> block, float millisDivisor, float modulo, Part part) {
            Unbaked[] holder = new Unbaked[1];
            MapCodec<Unbaked> codec = MapCodec.unit(() -> holder[0]);
            holder[0] = new Unbaked(codec, block, millisDivisor, modulo, part);
            return codec;
        }

        @Override
        public MapCodec<Unbaked> type() {
            return codec;
        }

        @Override
        public SpecialModelRenderer<Void> bake(SpecialModelRenderer.BakingContext context) {
            return new AnimatedBlockItemRenderer(block.get().defaultBlockState(), millisDivisor, modulo, part);
        }
    }
}
