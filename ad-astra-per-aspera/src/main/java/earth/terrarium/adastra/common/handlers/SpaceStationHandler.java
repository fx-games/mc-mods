package earth.terrarium.adastra.common.handlers;

import earth.terrarium.adastra.common.handlers.base.SaveHandler;
import earth.terrarium.adastra.common.handlers.base.SpaceStation;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

public class SpaceStationHandler extends SaveHandler {

    private final Map<UUID, Set<SpaceStation>> spaceStationData = new HashMap<>();

    @Override
    public void loadData(CompoundTag tag) {
        tag.keySet().forEach(id -> {
            ListTag stationsTag = tag.getListOrEmpty(id);
            Set<SpaceStation> stations = new HashSet<>();
            stationsTag.forEach(stationTag -> {
                CompoundTag stationCompoundTag = (CompoundTag) stationTag;
                Component name = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(stationCompoundTag.getStringOr("Name", "\"\""))).result().orElse(Component.empty());
                ChunkPos position = ChunkPos.unpack(stationCompoundTag.getLongOr("Position", 0L));
                stations.add(new SpaceStation(position, name));
            });
            spaceStationData.put(UUID.fromString(id), stations);
        });
    }

    @Override
    public void saveData(CompoundTag tag) {
        spaceStationData.forEach((id, stations) -> {
            ListTag ownerTag = new ListTag();
            for (var station : stations) {
                CompoundTag stationsTag = new CompoundTag();
                stationsTag.putString("Name", ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, station.name()).getOrThrow().toString());
                stationsTag.putLong("Position", station.position().pack());
                ownerTag.add(stationsTag);
            }
            tag.put(id.toString(), ownerTag);
        });
    }

    public static SpaceStationHandler read(ServerLevel level) {
        return read(level.getDataStorage(), SpaceStationHandler::new, "adastra_space_station_data");
    }

    public static Map<UUID, Set<SpaceStation>> getAllSpaceStations(ServerLevel level) {
        return read(level).spaceStationData;
    }


    public static void constructSpaceStation(ServerPlayer player, ServerLevel level, Component name) {
        var data = read(level).spaceStationData;
        var stations = data.computeIfAbsent(player.getUUID(), k -> new HashSet<>());
        stations.add(new SpaceStation(player.chunkPosition(), name));
    }

    public static boolean isInSpaceStation(ServerPlayer player, ServerLevel level) {
        for (var stations : read(level).spaceStationData.values()) {
            for (var station : stations) {
                if (station.position().getChessboardDistance(player.chunkPosition()) <= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Set<SpaceStation> getOwnedSpaceStations(ServerPlayer player, ServerLevel level) {
        return getOwnedSpaceStations(player.getUUID(), level);
    }

    public static Set<SpaceStation> getOwnedSpaceStations(UUID id, ServerLevel level) {
        return read(level).spaceStationData.getOrDefault(id, Set.of());
    }
}
