package earth.terrarium.adastra.common.blockentities.base.sideconfig;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.List;

public record ConfigurationEntry(
    ConfigurationType type,
    EnumMap<Direction, Configuration> sides,
    Component title) {

    public ConfigurationEntry(ConfigurationType type, Configuration defaultValue, Component title) {
        this(type, createConfiguration(defaultValue), title);
    }

    public Configuration get(Direction direction) {
        return this.sides.get(direction);
    }

    public void set(Direction direction, Configuration value) {
        this.sides.replace(direction, value);
    }

    public ConfigurationEntry copy() {
        return new ConfigurationEntry(type, new EnumMap<>(sides), title);
    }


    public static void save(ValueOutput output, List<ConfigurationEntry> sideConfig) {
        var list = output.childrenList("SideConfig");
        for (var entry : sideConfig) {
            ValueOutput entryTag = list.addChild();
            entryTag.putByte("Type", (byte) entry.type.ordinal());
            entry.sides.forEach((direction, configuration) ->
                entryTag.putByte(direction.getName(), (byte) configuration.ordinal()));
        }
    }

    public static void load(ValueInput input, List<ConfigurationEntry> sideConfig, List<ConfigurationEntry> defaultConfig) {
        sideConfig.clear();
        int i = 0;
        for (ValueInput entryTag : input.childrenListOrEmpty("SideConfig")) {
            ConfigurationType type = ConfigurationType.values()[entryTag.getByteOr("Type", (byte) 0)];

            EnumMap<Direction, Configuration> sides = new EnumMap<>(Direction.class);
            for (var direction : Direction.values()) {
                sides.put(direction, Configuration.values()[entryTag.getByteOr(direction.getName(), (byte) 0)]);
            }

            sideConfig.add(new ConfigurationEntry(type, sides, defaultConfig.get(i++).title()));
        }
    }

    private static EnumMap<Direction, Configuration> createConfiguration(Configuration value) {
        EnumMap<Direction, Configuration> configurations = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            configurations.put(direction, value);
        }
        return configurations;
    }
}
