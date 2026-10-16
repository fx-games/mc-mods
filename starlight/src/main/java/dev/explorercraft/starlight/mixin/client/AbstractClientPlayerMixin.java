package dev.explorercraft.starlight.mixin.client;

import dev.explorercraft.starlight.client.SpyglassZoom;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/// The spyglass's fixed 0.1 field of view becomes the adjustable one.
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @ModifyConstant(method = "getFieldOfViewModifier", constant = @Constant(floatValue = 0.1f))
    private float starlight$spyglassZoom(float vanilla) {
        return SpyglassZoom.factor();
    }
}
