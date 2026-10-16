package earth.terrarium.adastra.mixins.common.entities;

import earth.terrarium.adastra.common.entities.mob.ZombifiedMogler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.Zoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// 26.x: isTargetable is gone; target filtering now lives in findNearestValidAttackTarget.
@Mixin(Zoglin.class)
public abstract class ZoglinMixin {

    @Inject(method = "findNearestValidAttackTarget", at = @At("HEAD"), cancellable = true)
    private static void adastra$findNearestValidAttackTarget(ServerLevel level, Mob mob, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        if (mob instanceof ZombifiedMogler) {
            cir.setReturnValue(mob.getBrain()
                .getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .orElse(NearestVisibleLivingEntities.empty())
                .findClosest(target -> !(target instanceof ZombifiedMogler) && !target.is(EntityTypes.ZOGLIN) && !target.is(EntityTypes.CREEPER) && Sensor.isEntityAttackable(level, mob, target)));
        }
    }
}
