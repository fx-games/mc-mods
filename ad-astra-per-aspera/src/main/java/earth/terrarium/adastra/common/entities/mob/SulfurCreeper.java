package earth.terrarium.adastra.common.entities.mob;

import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import earth.terrarium.adastra.common.storage.ModifyOnlyContext;
import earth.terrarium.adastra.common.storage.FluidApi;
import earth.terrarium.adastra.common.storage.FluidAmounts;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class SulfurCreeper extends Creeper {

    public SulfurCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.@NotNull Builder createMobAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.35);
    }

    public void explodeCreeper() {
        if (!(this.level() instanceof ServerLevel level)) return;
        float power = isPowered() ? 2 : 1;
        this.dead = true;
        ServerExplosion explosion = explode(level, 3 * power);
        this.discard();

        for (Player player : explosion.getHitPlayers().keySet()) {
            var stack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (SpaceSuitItem.hasFullSet(player)) {
                ModifyOnlyContext itemContext = new ModifyOnlyContext(stack);
                if (!itemContext.isPresent(FluidApi.ITEM)) continue;
                var container = itemContext.find(FluidApi.ITEM);
                if (container == null) continue;
                long amount = Math.max(0, (long) ((7 - player.getPosition(0).distanceTo(player.getPosition(0))) * (FluidAmounts.toPlatformAmount(125))));
                container.extract(container.getResource(0), amount, false);
                player.setItemSlot(EquipmentSlot.CHEST, itemContext.stack());
            }
        }

        Collection<MobEffectInstance> effects = this.getActiveEffects();
        if (!effects.isEmpty()) {
            AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaEffectCloud.setRadius(2.5F);
            areaEffectCloud.setRadiusOnUse(-0.5F);
            areaEffectCloud.setWaitTime(10);
            areaEffectCloud.setDuration(areaEffectCloud.getDuration() / 2);
            areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float) areaEffectCloud.getDuration());

            for (MobEffectInstance mobEffectInstance : effects) {
                areaEffectCloud.addEffect(new MobEffectInstance(mobEffectInstance));
            }

            this.level().addFreshEntity(areaEffectCloud);
        }
    }

    // 26.x Level#explode no longer returns the explosion; mirror ServerLevel#explode (MOB interaction) to keep its hit players.
    private ServerExplosion explode(ServerLevel level, float radius) {
        Explosion.BlockInteraction interaction = !level.getGameRules().get(GameRules.MOB_GRIEFING) ? Explosion.BlockInteraction.KEEP
            : level.getGameRules().get(GameRules.MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
        Vec3 center = this.position();
        ServerExplosion explosion = new ServerExplosion(level, this, Explosion.getDefaultDamageSource(level, this), null, center, radius, false, interaction);
        int blockCount = explosion.explode();
        ParticleOptions particle = explosion.isSmall() ? ParticleTypes.EXPLOSION : ParticleTypes.EXPLOSION_EMITTER;
        WeightedList<ExplosionParticleInfo> blockParticles = WeightedList.<ExplosionParticleInfo>builder()
            .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
            .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
            .build();
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(center) < 4096.0) {
                Optional<Vec3> knockback = Optional.ofNullable(explosion.getHitPlayers().get(player));
                player.connection.send(new ClientboundExplodePacket(center, radius, blockCount, knockback, particle, SoundEvents.GENERIC_EXPLODE, blockParticles));
            }
        }
        return explosion;
    }
}
