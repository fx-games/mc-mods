package earth.terrarium.adastra.mixins.client;

import earth.terrarium.adastra.client.screens.base.AbstractContainerScreenExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements AbstractContainerScreenExtension {

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void adastra$renderPreSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        this.adastra$renderPreSlot(graphics, slot);
    }
}
