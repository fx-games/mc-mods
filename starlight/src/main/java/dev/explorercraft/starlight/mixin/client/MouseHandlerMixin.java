package dev.explorercraft.starlight.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.explorercraft.starlight.client.SpyglassZoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.ScrollWheelHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/// While looking through a spyglass the scroll wheel zooms instead of changing the hotbar slot,
/// and turning slows down with the zoom so the view moves the same on screen at every level.
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/ScrollWheelHandler;getNextScrollWheelSelection(DII)I"))
    private int starlight$zoom(double wheel, int current, int size, Operation<Integer> original) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.isScoping() && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            SpyglassZoom.scroll((int) Math.signum(wheel));
            return current;
        }
        return original.call(wheel, current, size);
    }

    /// Vanilla's scoped turn is tuned for 10x; scale it by how much narrower or wider the view is.
    /// The second read of each field in turnPlayer is the one in the scoping branch.
    @ModifyExpressionValue(method = "turnPlayer", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/MouseHandler;accumulatedDX:D", ordinal = 1))
    private double starlight$scaleTurnX(double dx) {
        return dx * SpyglassZoom.factor() / SpyglassZoom.VANILLA;
    }

    @ModifyExpressionValue(method = "turnPlayer", at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/MouseHandler;accumulatedDY:D", ordinal = 1))
    private double starlight$scaleTurnY(double dy) {
        return dy * SpyglassZoom.factor() / SpyglassZoom.VANILLA;
    }
}
