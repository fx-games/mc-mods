package dev.explorercraft.photosafari.mixin.client;

import dev.explorercraft.photosafari.client.PhotoSafariClient;
import me.chrr.camerapture.item.CameraItem;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Loot mode never saves a picture, so it has no business needing paper — but Camerapture
/// gates the whole shutter on this check, so without paper the trigger did nothing at all
/// and the viewfinder sat there saying NO PAPER. Saying yes in loot mode lets the shutter
/// through to our own hook, which answers FAIL and so stops the picture right there: the
/// loot goes out, no paper is spent, nothing is stored. Client side only, because the mode
/// is the player's own: the server still checks paper for every real photograph.
@Mixin(CameraItem.class)
public abstract class CameraItemMixin {
    @Inject(method = "canTakePicture", at = @At("HEAD"), cancellable = true)
    private static void photosafari$lootModeNeedsNoPaper(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (PhotoSafariClient.isLootMode()) {
            cir.setReturnValue(true);
        }
    }
}
