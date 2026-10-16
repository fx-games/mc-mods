package earth.terrarium.adastra.mixins.fabric.common.multipart;

import earth.terrarium.adastra.common.entities.multipart.MultipartPartEntity;
import earth.terrarium.adastra.common.entities.multipart.MultipartPartsHolder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

// 26.x: hooks the public getEntities methods instead of their (renumbered) lambdas, like vanilla dragon parts.
@Mixin(Level.class)
public class LevelMixin implements MultipartPartsHolder {

    @Unique
    private final Int2ObjectMap<Entity> adastra$multipartEntityParts = new Int2ObjectOpenHashMap<>();

    @Override
    public Int2ObjectMap<Entity> adastra$getParts() {
        return adastra$multipartEntityParts;
    }

    @Inject(
        method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
        at = @At("RETURN")
    )
    private void adastra$getEntities(@Nullable Entity except, AABB bb, Predicate<? super Entity> selector, CallbackInfoReturnable<List<Entity>> cir) {
        for (Entity part : adastra$multipartEntityParts.values()) {
            Entity parent = ((MultipartPartEntity<?>) part).getMultiParts();
            if (part != except && parent != except && bb.intersects(part.getBoundingBox()) && selector.test(part)) {
                cir.getReturnValue().add(part);
            }
        }
    }

    @Inject(
        method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V",
        at = @At("TAIL")
    )
    private <T extends Entity> void adastra$getEntities(
        EntityTypeTest<Entity, T> type, AABB bb, Predicate<? super T> selector, List<? super T> output, int maxResults, CallbackInfo ci
    ) {
        for (Entity part : adastra$multipartEntityParts.values()) {
            if (output.size() >= maxResults) return;
            if (!bb.intersects(part.getBoundingBox())) continue;
            T cast = type.tryCast(part);
            if (cast != null && selector.test(cast)) {
                output.add(cast);
            }
        }
    }
}
