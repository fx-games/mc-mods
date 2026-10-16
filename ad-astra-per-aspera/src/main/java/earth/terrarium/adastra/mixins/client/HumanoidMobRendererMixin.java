package earth.terrarium.adastra.mixins.client;

import earth.terrarium.adastra.client.AdAstraClient;
import earth.terrarium.adastra.common.entities.vehicles.Vehicle;
import earth.terrarium.adastra.common.tags.ModItemTags;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin {

    @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void adastra$extractHumanoidRenderState(LivingEntity entity, HumanoidRenderState state, float partialTicks, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        if (entity.getVehicle() instanceof Vehicle vehicle) {
            state.isPassenger = vehicle.shouldSit(); // disable the sitting pose while standing in a rocket
        }
        Integer held = null;
        if (entity.getMainHandItem().is(ModItemTags.HELD_OVER_HEAD)) held = 1;
        else if (entity.getOffhandItem().is(ModItemTags.HELD_OVER_HEAD)) held = -1;
        state.setData(AdAstraClient.HELD_OVER_HEAD, held);
    }
}
