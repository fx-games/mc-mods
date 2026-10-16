package earth.terrarium.adastra.common.entities.mob.lunarians;

import earth.terrarium.adastra.api.planets.PlanetApi;
import earth.terrarium.adastra.common.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.WanderingTraderData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class LunarianWanderingTraderSpawner implements CustomSpawner {

    public static final int DEFAULT_SPAWN_DELAY = 24000;
    private static final int DEFAULT_SPAWN_TIMER = 1200;
    private final Random random = new Random();
    // 26.x: wandering trader delay/chance moved from ServerLevelData to the WanderingTraderData saved data.
    private final SavedDataStorage storage;
    private int spawnTimer;
    private int spawnDelay;
    private int spawnChance;
    private boolean initialized;

    public LunarianWanderingTraderSpawner(SavedDataStorage storage) {
        this.storage = storage;
        this.spawnTimer = DEFAULT_SPAWN_TIMER;
    }

    @Override
    public void tick(ServerLevel level, boolean spawnEnemies) {
        WanderingTraderData properties = this.storage.computeIfAbsent(WanderingTraderData.TYPE);
        if (!this.initialized) {
            this.initialized = true;
            this.spawnDelay = properties.spawnDelay();
            this.spawnChance = properties.spawnChance();
            if (this.spawnDelay == 0 && this.spawnChance == 0) {
                this.spawnDelay = DEFAULT_SPAWN_DELAY;
                properties.setSpawnDelay(this.spawnDelay);
                this.spawnChance = 25;
                properties.setSpawnChance(this.spawnChance);
            }
        }
        if (!level.getGameRules().get(GameRules.SPAWN_WANDERING_TRADERS)) return;
        if (--this.spawnTimer > 0) return;

        this.spawnTimer = DEFAULT_SPAWN_TIMER;
        this.spawnDelay -= DEFAULT_SPAWN_TIMER;
        properties.setSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) return;
        this.spawnDelay = DEFAULT_SPAWN_DELAY;
        if (!level.getGameRules().get(GameRules.SPAWN_MOBS)) return;
        int i = this.spawnChance;
        this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75);
        properties.setSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > i) return;
        if (this.trySpawn(level)) {
            this.spawnChance = 25;
        }
    }

    private boolean trySpawn(ServerLevel level) {
        ServerPlayer playerEntity = level.getRandomPlayer();
        if (playerEntity == null) return true;
        if (this.random.nextInt(10) != 0) return false;
        if (!PlanetApi.API.isPlanet(level.dimension())) return false;
        if (Level.OVERWORLD.equals(level.dimension())) return false;

        BlockPos blockPos = playerEntity.blockPosition();
        PoiManager pointOfInterestStorage = level.getPoiManager();
        Optional<BlockPos> optional = pointOfInterestStorage.find(holder -> holder.is(PoiTypes.MEETING), pos -> true, blockPos, 48, PoiManager.Occupancy.ANY);
        BlockPos blockPos2 = optional.orElse(blockPos);
        BlockPos blockPos3 = this.getNearbySpawnPos(level, blockPos2, 48);
        if (blockPos3 != null && this.doesNotSuffocateAt(level, blockPos3)) {
            if (level.getBiome(blockPos3).is((BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS))) {
                return false;
            }

            WanderingTrader wanderingTraderEntity = ModEntityTypes.LUNARIAN_WANDERING_TRADER.get().spawn(level, blockPos3, EntitySpawnReason.EVENT);
            if (wanderingTraderEntity != null) {

                wanderingTraderEntity.setDespawnDelay(48000);
                wanderingTraderEntity.setWanderTarget(blockPos2);
                wanderingTraderEntity.setHomeTo(blockPos2, 16);
                return true;
            }
        }
        return false;
    }

    @Nullable
    private BlockPos getNearbySpawnPos(LevelReader level, BlockPos pos, int range) {
        BlockPos blockPos = null;
        SpawnPlacementType placementType = SpawnPlacements.getPlacementType(ModEntityTypes.LUNARIAN_WANDERING_TRADER.get());
        for (int i = 0; i < 10; ++i) {
            int k;
            int j = pos.getX() + this.random.nextInt(range * 2) - range;
            BlockPos blockPos2 = BlockPos.containing(j, level.getHeight(Heightmap.Types.WORLD_SURFACE, j, k = pos.getZ() + this.random.nextInt(range * 2) - range), k);
            if (!placementType.isSpawnPositionOk(level, blockPos2, ModEntityTypes.LUNARIAN_WANDERING_TRADER.get()))
                continue;
            blockPos = blockPos2;
            break;
        }
        return blockPos;
    }

    private boolean doesNotSuffocateAt(BlockGetter level, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.betweenClosed(pos, pos.offset(1, 2, 1))) {
            if (level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty())
                continue;
            return false;
        }
        return true;
    }
}