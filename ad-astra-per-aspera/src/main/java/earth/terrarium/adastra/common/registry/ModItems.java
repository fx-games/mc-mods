package earth.terrarium.adastra.common.registry;

import com.teamresourceful.resourcefullib.common.fluid.ResourcefulBucketItem;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.items.*;
import earth.terrarium.adastra.common.items.armor.JetSuitItem;
import earth.terrarium.adastra.common.items.armor.NetheriteSpaceSuitItem;
import earth.terrarium.adastra.common.items.armor.SpaceSuitItem;
import earth.terrarium.adastra.common.items.armor.base.CustomDyeableArmorItem;
import earth.terrarium.adastra.common.items.armor.materials.ModArmorMaterials;
import earth.terrarium.adastra.common.items.machines.EnergizerBlockItem;
import earth.terrarium.adastra.common.items.rendered.RenderedBlockItem;
import earth.terrarium.adastra.common.items.rendered.TooltipRenderedBlockItem;
import earth.terrarium.adastra.common.items.vehicles.RocketItem;
import earth.terrarium.adastra.common.items.vehicles.RoverItem;
import earth.terrarium.adastra.common.tags.ModPaintingVariantTags;
import earth.terrarium.adastra.common.utils.PlatformUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;

@SuppressWarnings("unused")
public class ModItems {

    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, AdAstra.MOD_ID);

    // Items with item textures.
    public static final ResourcefulRegistry<Item> BASIC_ITEMS = ResourcefulRegistries.create(ITEMS);

    public static final ResourcefulRegistry<Item> SLIDING_DOORS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> GLOBES = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> FLAGS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> PIPES = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> VEHICLES = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> SPAWN_EGGS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> INDUSTRIAL_LAMPS = ResourcefulRegistries.create(ITEMS);
    public static final ResourcefulRegistry<Item> SMALL_INDUSTRIAL_LAMPS = ResourcefulRegistries.create(ITEMS);

    public static final RegistryEntry<Item> TIER_1_ROCKET = VEHICLES.register("tier_1_rocket", () -> new RocketItem(ModEntityTypes.TIER_1_ROCKET::get, props("tier_1_rocket").stacksTo(1).fireResistant()));
    public static final RegistryEntry<Item> TIER_2_ROCKET = VEHICLES.register("tier_2_rocket", () -> new RocketItem(ModEntityTypes.TIER_2_ROCKET::get, props("tier_2_rocket").stacksTo(1).fireResistant()));
    public static final RegistryEntry<Item> TIER_3_ROCKET = VEHICLES.register("tier_3_rocket", () -> new RocketItem(ModEntityTypes.TIER_3_ROCKET::get, props("tier_3_rocket").stacksTo(1).fireResistant()));
    public static final RegistryEntry<Item> TIER_4_ROCKET = VEHICLES.register("tier_4_rocket", () -> new RocketItem(ModEntityTypes.TIER_4_ROCKET::get, props("tier_4_rocket").stacksTo(1).fireResistant()));
    public static final RegistryEntry<Item> ROVER = VEHICLES.register("tier_1_rover", () -> new RoverItem(ModEntityTypes.ROVER::get, props("tier_1_rover").stacksTo(1).fireResistant()));
    public static final RegistryEntry<Item> LAUNCH_PAD = ITEMS.register("launch_pad", () -> new ModBlockItem(ModBlocks.LAUNCH_PAD.get(), props("launch_pad").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> STEEL_CABLE = PIPES.register("steel_cable", () -> new ModBlockItem(ModBlocks.STEEL_CABLE.get(), props("steel_cable").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_CABLE = PIPES.register("desh_cable", () -> new ModBlockItem(ModBlocks.DESH_CABLE.get(), props("desh_cable").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_FLUID_PIPE = PIPES.register("desh_fluid_pipe", () -> new ModBlockItem(ModBlocks.DESH_FLUID_PIPE.get(), props("desh_fluid_pipe").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_FLUID_PIPE = PIPES.register("ostrum_fluid_pipe", () -> new ModBlockItem(ModBlocks.OSTRUM_FLUID_PIPE.get(), props("ostrum_fluid_pipe").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> CABLE_DUCT = ITEMS.register("cable_duct", () -> new ModBlockItem(ModBlocks.CABLE_DUCT.get(), props("cable_duct").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> FLUID_PIPE_DUCT = ITEMS.register("fluid_pipe_duct", () -> new ModBlockItem(ModBlocks.FLUID_PIPE_DUCT.get(), props("fluid_pipe_duct").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> COAL_GENERATOR = ITEMS.register("coal_generator", () -> new TooltipBlockItem(ModBlocks.COAL_GENERATOR.get(), ConstantComponents.COAL_GENERATOR_INFO, props("coal_generator").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> COMPRESSOR = ITEMS.register("compressor", () -> new TooltipBlockItem(ModBlocks.COMPRESSOR.get(), ConstantComponents.COMPRESSOR_INFO, props("compressor").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ETRIONIC_BLAST_FURNACE = ITEMS.register("etrionic_blast_furnace", () -> new TooltipBlockItem(ModBlocks.ETRIONIC_BLAST_FURNACE.get(), ConstantComponents.ETRIONIC_BLAST_FURNACE_INFO, props("etrionic_blast_furnace").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> NASA_WORKBENCH = ITEMS.register("nasa_workbench", () -> new TooltipBlockItem(ModBlocks.NASA_WORKBENCH.get(), ConstantComponents.NASA_WORKBENCH_INFO, props("nasa_workbench").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> FUEL_REFINERY = ITEMS.register("fuel_refinery", () -> new TooltipBlockItem(ModBlocks.FUEL_REFINERY.get(), ConstantComponents.FUEL_REFINERY_INFO, props("fuel_refinery").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OXYGEN_LOADER = ITEMS.register("oxygen_loader", () -> new TooltipBlockItem(ModBlocks.OXYGEN_LOADER.get(), ConstantComponents.OXYGEN_LOADER_INFO, props("oxygen_loader").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SOLAR_PANEL = ITEMS.register("solar_panel", () -> new TooltipBlockItem(ModBlocks.SOLAR_PANEL.get(), ConstantComponents.SOLAR_PANEL_INFO, props("solar_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> WATER_PUMP = ITEMS.register("water_pump", () -> new TooltipBlockItem(ModBlocks.WATER_PUMP.get(), ConstantComponents.WATER_PUMP_INFO, props("water_pump").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OXYGEN_DISTRIBUTOR = ITEMS.register("oxygen_distributor", () -> new TooltipRenderedBlockItem(ModBlocks.OXYGEN_DISTRIBUTOR.get(), ConstantComponents.OXYGEN_DISTRIBUTOR_INFO, props("oxygen_distributor").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GRAVITY_NORMALIZER = ITEMS.register("gravity_normalizer", () -> new TooltipRenderedBlockItem(ModBlocks.GRAVITY_NORMALIZER.get(), ConstantComponents.GRAVITY_NORMALIZER_INFO, props("gravity_normalizer").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENERGIZER = ITEMS.register("energizer", () -> new EnergizerBlockItem(ModBlocks.ENERGIZER.get(), props("energizer").useBlockDescriptionPrefix().stacksTo(1)));
    public static final RegistryEntry<Item> CRYO_FREEZER = ITEMS.register("cryo_freezer", () -> new TooltipBlockItem(ModBlocks.CRYO_FREEZER.get(), ConstantComponents.CRYO_FREEZER_INFO, props("cryo_freezer").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OXYGEN_SENSOR = ITEMS.register("oxygen_sensor", () -> new TooltipBlockItem(ModBlocks.OXYGEN_SENSOR.get(), ConstantComponents.OXYGEN_SENSOR_INFO, props("oxygen_sensor").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> WHITE_FLAG = FLAGS.register("white_flag", () -> new ModBlockItem(ModBlocks.WHITE_FLAG.get(), props("white_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ORANGE_FLAG = FLAGS.register("orange_flag", () -> new ModBlockItem(ModBlocks.ORANGE_FLAG.get(), props("orange_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MAGENTA_FLAG = FLAGS.register("magenta_flag", () -> new ModBlockItem(ModBlocks.MAGENTA_FLAG.get(), props("magenta_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> LIGHT_BLUE_FLAG = FLAGS.register("light_blue_flag", () -> new ModBlockItem(ModBlocks.LIGHT_BLUE_FLAG.get(), props("light_blue_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> YELLOW_FLAG = FLAGS.register("yellow_flag", () -> new ModBlockItem(ModBlocks.YELLOW_FLAG.get(), props("yellow_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> LIME_FLAG = FLAGS.register("lime_flag", () -> new ModBlockItem(ModBlocks.LIME_FLAG.get(), props("lime_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PINK_FLAG = FLAGS.register("pink_flag", () -> new ModBlockItem(ModBlocks.PINK_FLAG.get(), props("pink_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GRAY_FLAG = FLAGS.register("gray_flag", () -> new ModBlockItem(ModBlocks.GRAY_FLAG.get(), props("gray_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> LIGHT_GRAY_FLAG = FLAGS.register("light_gray_flag", () -> new ModBlockItem(ModBlocks.LIGHT_GRAY_FLAG.get(), props("light_gray_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CYAN_FLAG = FLAGS.register("cyan_flag", () -> new ModBlockItem(ModBlocks.CYAN_FLAG.get(), props("cyan_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PURPLE_FLAG = FLAGS.register("purple_flag", () -> new ModBlockItem(ModBlocks.PURPLE_FLAG.get(), props("purple_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> BLUE_FLAG = FLAGS.register("blue_flag", () -> new ModBlockItem(ModBlocks.BLUE_FLAG.get(), props("blue_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> BROWN_FLAG = FLAGS.register("brown_flag", () -> new ModBlockItem(ModBlocks.BROWN_FLAG.get(), props("brown_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GREEN_FLAG = FLAGS.register("green_flag", () -> new ModBlockItem(ModBlocks.GREEN_FLAG.get(), props("green_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> RED_FLAG = FLAGS.register("red_flag", () -> new ModBlockItem(ModBlocks.RED_FLAG.get(), props("red_flag").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> BLACK_FLAG = FLAGS.register("black_flag", () -> new ModBlockItem(ModBlocks.BLACK_FLAG.get(), props("black_flag").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> EARTH_GLOBE = GLOBES.register("earth_globe", () -> new RenderedBlockItem(ModBlocks.EARTH_GLOBE.get(), props("earth_globe").useBlockDescriptionPrefix().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryEntry<Item> MOON_GLOBE = GLOBES.register("moon_globe", () -> new RenderedBlockItem(ModBlocks.MOON_GLOBE.get(), props("moon_globe").useBlockDescriptionPrefix().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryEntry<Item> MARS_GLOBE = GLOBES.register("mars_globe", () -> new RenderedBlockItem(ModBlocks.MARS_GLOBE.get(), props("mars_globe").useBlockDescriptionPrefix().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryEntry<Item> MERCURY_GLOBE = GLOBES.register("mercury_globe", () -> new RenderedBlockItem(ModBlocks.MERCURY_GLOBE.get(), props("mercury_globe").useBlockDescriptionPrefix().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryEntry<Item> VENUS_GLOBE = GLOBES.register("venus_globe", () -> new RenderedBlockItem(ModBlocks.VENUS_GLOBE.get(), props("venus_globe").useBlockDescriptionPrefix().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryEntry<Item> GLACIO_GLOBE = GLOBES.register("glacio_globe", () -> new RenderedBlockItem(ModBlocks.GLACIO_GLOBE.get(), props("glacio_globe").useBlockDescriptionPrefix().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryEntry<Item> RADIO = ITEMS.register("radio", () -> new TooltipBlockItem(ModBlocks.RADIO.get(), ConstantComponents.RADIO_INFO, props("radio").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> SPACE_HELMET = BASIC_ITEMS.register("space_helmet", () -> new CustomDyeableArmorItem(ModArmorMaterials.SPACE_SUIT, ArmorType.HELMET, props("space_helmet")));
    public static final RegistryEntry<Item> SPACE_SUIT = BASIC_ITEMS.register("space_suit", () -> new SpaceSuitItem(ModArmorMaterials.SPACE_SUIT, ArmorType.CHESTPLATE, 1000, props("space_suit")));
    public static final RegistryEntry<Item> SPACE_PANTS = BASIC_ITEMS.register("space_pants", () -> new CustomDyeableArmorItem(ModArmorMaterials.SPACE_SUIT, ArmorType.LEGGINGS, props("space_pants")));
    public static final RegistryEntry<Item> SPACE_BOOTS = BASIC_ITEMS.register("space_boots", () -> new CustomDyeableArmorItem(ModArmorMaterials.SPACE_SUIT, ArmorType.BOOTS, props("space_boots")));

    public static final RegistryEntry<Item> NETHERITE_SPACE_HELMET = ITEMS.register("netherite_space_helmet", () -> new CustomDyeableArmorItem(ModArmorMaterials.NETHERITE_SPACE_SUIT, ArmorType.HELMET, props("netherite_space_helmet").fireResistant()));
    public static final RegistryEntry<Item> NETHERITE_SPACE_SUIT = BASIC_ITEMS.register("netherite_space_suit", () -> new NetheriteSpaceSuitItem(ModArmorMaterials.NETHERITE_SPACE_SUIT, ArmorType.CHESTPLATE, 2000, props("netherite_space_suit").fireResistant()));
    public static final RegistryEntry<Item> NETHERITE_SPACE_PANTS = BASIC_ITEMS.register("netherite_space_pants", () -> new CustomDyeableArmorItem(ModArmorMaterials.NETHERITE_SPACE_SUIT, ArmorType.LEGGINGS, props("netherite_space_pants").fireResistant()));
    public static final RegistryEntry<Item> NETHERITE_SPACE_BOOTS = BASIC_ITEMS.register("netherite_space_boots", () -> new CustomDyeableArmorItem(ModArmorMaterials.NETHERITE_SPACE_SUIT, ArmorType.BOOTS, props("netherite_space_boots").fireResistant()));

    public static final RegistryEntry<Item> JET_SUIT_HELMET = ITEMS.register("jet_suit_helmet", () -> new CustomDyeableArmorItem(ModArmorMaterials.JET_SUIT, ArmorType.HELMET, props("jet_suit_helmet").fireResistant()));
    public static final RegistryEntry<Item> JET_SUIT = BASIC_ITEMS.register("jet_suit", () -> new JetSuitItem(ModArmorMaterials.JET_SUIT, ArmorType.CHESTPLATE, 4000, 1_000_000, props("jet_suit").fireResistant()));
    public static final RegistryEntry<Item> JET_SUIT_PANTS = BASIC_ITEMS.register("jet_suit_pants", () -> new CustomDyeableArmorItem(ModArmorMaterials.JET_SUIT, ArmorType.LEGGINGS, props("jet_suit_pants").fireResistant()));
    public static final RegistryEntry<Item> JET_SUIT_BOOTS = BASIC_ITEMS.register("jet_suit_boots", () -> new CustomDyeableArmorItem(ModArmorMaterials.JET_SUIT, ArmorType.BOOTS, props("jet_suit_boots").fireResistant()));

    public static final RegistryEntry<Item> TI_69 = ITEMS.register("ti_69", () -> new Ti69Item(props("ti_69").stacksTo(1)));
    public static final RegistryEntry<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(props("wrench").stacksTo(1)));
    public static final RegistryEntry<Item> ZIP_GUN = ITEMS.register("zip_gun", () -> new ZipGunItem(props("zip_gun").stacksTo(1)));

    public static final RegistryEntry<Item> SPACE_PAINTING = BASIC_ITEMS.register("space_painting", () -> new SpacePaintingItem(props("space_painting"), ModPaintingVariants.EARTH, ModPaintingVariantTags.SPACE_PAINTINGS));

    public static final RegistryEntry<Item> CHEESE = BASIC_ITEMS.register("cheese", () -> new Item(props("cheese")
        .food(new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.0f)
            .build())));

    public static final RegistryEntry<Item> ETRIONIC_CAPACITOR = ITEMS.register("etrionic_capacitor", () -> new EtrionicCapacitorItem(props("etrionic_capacitor").stacksTo(1)));
    public static final RegistryEntry<Item> GAS_TANK = BASIC_ITEMS.register("gas_tank", () -> new GasTankItem(props("gas_tank").stacksTo(1), 1000, 10));
    public static final RegistryEntry<Item> LARGE_GAS_TANK = BASIC_ITEMS.register("large_gas_tank", () -> new GasTankItem(props("large_gas_tank").stacksTo(1), 3000, 50));

    public static final RegistryEntry<Item> IRON_PLATE = BASIC_ITEMS.register("iron_plate", () -> new Item(props("iron_plate")));
    public static final RegistryEntry<Item> IRON_ROD = BASIC_ITEMS.register("iron_rod", () -> new Item(props("iron_rod")));

    public static final RegistryEntry<Item> STEEL_INGOT = BASIC_ITEMS.register("steel_ingot", () -> new Item(props("steel_ingot")));
    public static final RegistryEntry<Item> STEEL_NUGGET = BASIC_ITEMS.register("steel_nugget", () -> new Item(props("steel_nugget")));
    public static final RegistryEntry<Item> STEEL_PLATE = BASIC_ITEMS.register("steel_plate", () -> new Item(props("steel_plate")));
    public static final RegistryEntry<Item> STEEL_ROD = BASIC_ITEMS.register("steel_rod", () -> new Item(props("steel_rod")));

    public static final RegistryEntry<Item> ETRIUM_INGOT = BASIC_ITEMS.register("etrium_ingot", () -> new Item(props("etrium_ingot")));
    public static final RegistryEntry<Item> ETRIUM_NUGGET = BASIC_ITEMS.register("etrium_nugget", () -> new Item(props("etrium_nugget")));
    public static final RegistryEntry<Item> ETRIUM_PLATE = BASIC_ITEMS.register("etrium_plate", () -> new Item(props("etrium_plate")));
    public static final RegistryEntry<Item> ETRIUM_ROD = BASIC_ITEMS.register("etrium_rod", () -> new Item(props("etrium_rod")));
    public static final RegistryEntry<Item> ETRIONIC_CORE = BASIC_ITEMS.register("etrionic_core", () -> new Item(props("etrionic_core")));

    public static final RegistryEntry<Item> DESH_INGOT = BASIC_ITEMS.register("desh_ingot", () -> new Item(props("desh_ingot")));
    public static final RegistryEntry<Item> DESH_NUGGET = BASIC_ITEMS.register("desh_nugget", () -> new Item(props("desh_nugget")));
    public static final RegistryEntry<Item> DESH_PLATE = BASIC_ITEMS.register("desh_plate", () -> new Item(props("desh_plate")));

    public static final RegistryEntry<Item> OSTRUM_INGOT = BASIC_ITEMS.register("ostrum_ingot", () -> new Item(props("ostrum_ingot")));
    public static final RegistryEntry<Item> OSTRUM_NUGGET = BASIC_ITEMS.register("ostrum_nugget", () -> new Item(props("ostrum_nugget")));
    public static final RegistryEntry<Item> OSTRUM_PLATE = BASIC_ITEMS.register("ostrum_plate", () -> new Item(props("ostrum_plate")));

    public static final RegistryEntry<Item> CALORITE_INGOT = BASIC_ITEMS.register("calorite_ingot", () -> new Item(props("calorite_ingot")));
    public static final RegistryEntry<Item> CALORITE_NUGGET = BASIC_ITEMS.register("calorite_nugget", () -> new Item(props("calorite_nugget")));
    public static final RegistryEntry<Item> CALORITE_PLATE = BASIC_ITEMS.register("calorite_plate", () -> new Item(props("calorite_plate")));

    public static final RegistryEntry<Item> RAW_DESH = BASIC_ITEMS.register("raw_desh", () -> new Item(props("raw_desh")));
    public static final RegistryEntry<Item> RAW_OSTRUM = BASIC_ITEMS.register("raw_ostrum", () -> new Item(props("raw_ostrum")));
    public static final RegistryEntry<Item> RAW_CALORITE = BASIC_ITEMS.register("raw_calorite", () -> new Item(props("raw_calorite")));

    public static final RegistryEntry<Item> PHOTOVOLTAIC_ETRIUM_CELL = BASIC_ITEMS.register("photovoltaic_etrium_cell", () -> new Item(props("photovoltaic_etrium_cell")));
    public static final RegistryEntry<Item> PHOTOVOLTAIC_VESNIUM_CELL = BASIC_ITEMS.register("photovoltaic_vesnium_cell", () -> new Item(props("photovoltaic_vesnium_cell")));

    public static final RegistryEntry<Item> OXYGEN_GEAR = BASIC_ITEMS.register("oxygen_gear", () -> new Item(props("oxygen_gear")));
    public static final RegistryEntry<Item> WHEEL = BASIC_ITEMS.register("wheel", () -> new Item(props("wheel")));
    public static final RegistryEntry<Item> ENGINE_FRAME = BASIC_ITEMS.register("engine_frame", () -> new Item(props("engine_frame")));
    public static final RegistryEntry<Item> FAN = BASIC_ITEMS.register("fan", () -> new Item(props("fan")));
    public static final RegistryEntry<Item> ROCKET_NOSE_CONE = BASIC_ITEMS.register("rocket_nose_cone", () -> new Item(props("rocket_nose_cone")));
    public static final RegistryEntry<Item> STEEL_ENGINE = BASIC_ITEMS.register("steel_engine", () -> new Item(props("steel_engine")));
    public static final RegistryEntry<Item> DESH_ENGINE = BASIC_ITEMS.register("desh_engine", () -> new Item(props("desh_engine")));
    public static final RegistryEntry<Item> OSTRUM_ENGINE = BASIC_ITEMS.register("ostrum_engine", () -> new Item(props("ostrum_engine")));
    public static final RegistryEntry<Item> CALORITE_ENGINE = BASIC_ITEMS.register("calorite_engine", () -> new Item(props("calorite_engine")));
    public static final RegistryEntry<Item> STEEL_TANK = BASIC_ITEMS.register("steel_tank", () -> new Item(props("steel_tank")));
    public static final RegistryEntry<Item> DESH_TANK = BASIC_ITEMS.register("desh_tank", () -> new Item(props("desh_tank")));
    public static final RegistryEntry<Item> OSTRUM_TANK = BASIC_ITEMS.register("ostrum_tank", () -> new Item(props("ostrum_tank")));
    public static final RegistryEntry<Item> CALORITE_TANK = BASIC_ITEMS.register("calorite_tank", () -> new Item(props("calorite_tank")));
    public static final RegistryEntry<Item> ROCKET_FIN = BASIC_ITEMS.register("rocket_fin", () -> new Item(props("rocket_fin")));

    public static final RegistryEntry<Item> ICE_SHARD = BASIC_ITEMS.register("ice_shard", () -> new Item(props("ice_shard")));
    public static final RegistryEntry<Item> AERONOS_MUSHROOM = BASIC_ITEMS.register("aeronos_mushroom", () -> new ModBlockItem(ModBlocks.AERONOS_MUSHROOM.get(), props("aeronos_mushroom").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_MUSHROOM = BASIC_ITEMS.register("strophar_mushroom", () -> new ModBlockItem(ModBlocks.STROPHAR_MUSHROOM.get(), props("strophar_mushroom").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> OXYGEN_BUCKET = ModItems.BASIC_ITEMS.register("oxygen_bucket", () -> new ResourcefulBucketItem(
        ModFluids.OXYGEN_FLUID_TYPE.get(),
        props("oxygen_bucket").craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final RegistryEntry<Item> HYDROGEN_BUCKET = ModItems.BASIC_ITEMS.register("hydrogen_bucket", () -> new ResourcefulBucketItem(
        ModFluids.HYDROGEN_FLUID_TYPE.get(),
        props("hydrogen_bucket").craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final RegistryEntry<Item> OIL_BUCKET = ModItems.BASIC_ITEMS.register("oil_bucket", () -> new ResourcefulBucketItem(
        ModFluids.OIL_FLUID_TYPE.get(),
        props("oil_bucket").craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final RegistryEntry<Item> FUEL_BUCKET = ModItems.BASIC_ITEMS.register("fuel_bucket", () -> new ResourcefulBucketItem(
        ModFluids.FUEL_FLUID_TYPE.get(),
        props("fuel_bucket").craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final RegistryEntry<Item> CRYO_FUEL_BUCKET = ModItems.BASIC_ITEMS.register("cryo_fuel_bucket", () -> new ResourcefulBucketItem(
        ModFluids.CRYO_FUEL_FLUID_TYPE.get(),
        props("cryo_fuel_bucket").craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final RegistryEntry<Item> CHEESE_BLOCK = ITEMS.register("cheese_block", () -> new ModBlockItem(ModBlocks.CHEESE_BLOCK.get(), props("cheese_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SKY_STONE = ITEMS.register("sky_stone", () -> new ModBlockItem(ModBlocks.SKY_STONE.get(), props("sky_stone").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> VENT = ITEMS.register("vent", () -> new TooltipBlockItem(ModBlocks.VENT.get(), ConstantComponents.VENT_INFO, props("vent").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_FACTORY_BLOCK = ITEMS.register("iron_factory_block", () -> new ModBlockItem(ModBlocks.IRON_FACTORY_BLOCK.get(), props("iron_factory_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENCASED_IRON_BLOCK = ITEMS.register("encased_iron_block", () -> new ModBlockItem(ModBlocks.ENCASED_IRON_BLOCK.get(), props("encased_iron_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PLATEBLOCK = ITEMS.register("iron_plateblock", () -> new ModBlockItem(ModBlocks.IRON_PLATEBLOCK.get(), props("iron_plateblock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PANEL = ITEMS.register("iron_panel", () -> new ModBlockItem(ModBlocks.IRON_PANEL.get(), props("iron_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PLATING = ITEMS.register("iron_plating", () -> new ModBlockItem(ModBlocks.IRON_PLATING.get(), props("iron_plating").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PLATING_STAIRS = ITEMS.register("iron_plating_stairs", () -> new ModBlockItem(ModBlocks.IRON_PLATING_STAIRS.get(), props("iron_plating_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PLATING_SLAB = ITEMS.register("iron_plating_slab", () -> new ModBlockItem(ModBlocks.IRON_PLATING_SLAB.get(), props("iron_plating_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PILLAR = ITEMS.register("iron_pillar", () -> new ModBlockItem(ModBlocks.IRON_PILLAR.get(), props("iron_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLOWING_IRON_PILLAR = ITEMS.register("glowing_iron_pillar", () -> new ModBlockItem(ModBlocks.GLOWING_IRON_PILLAR.get(), props("glowing_iron_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARKED_IRON_PILLAR = ITEMS.register("marked_iron_pillar", () -> new ModBlockItem(ModBlocks.MARKED_IRON_PILLAR.get(), props("marked_iron_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PLATING_BUTTON = ITEMS.register("iron_plating_button", () -> new ModBlockItem(ModBlocks.IRON_PLATING_BUTTON.get(), props("iron_plating_button").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_PLATING_PRESSURE_PLATE = ITEMS.register("iron_plating_pressure_plate", () -> new ModBlockItem(ModBlocks.IRON_PLATING_PRESSURE_PLATE.get(), props("iron_plating_pressure_plate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> IRON_SLIDING_DOOR = SLIDING_DOORS.register("iron_sliding_door", () -> new ModBlockItem(ModBlocks.IRON_SLIDING_DOOR.get(), props("iron_sliding_door").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> ETRIUM_FACTORY_BLOCK = ITEMS.register("etrium_factory_block", () -> new ModBlockItem(ModBlocks.ETRIUM_FACTORY_BLOCK.get(), props("etrium_factory_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENCASED_ETRIUM_BLOCK = ITEMS.register("encased_etrium_block", () -> new ModBlockItem(ModBlocks.ENCASED_ETRIUM_BLOCK.get(), props("encased_etrium_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ETRIUM_PLATEBLOCK = ITEMS.register("etrium_plateblock", () -> new ModBlockItem(ModBlocks.ETRIUM_PLATEBLOCK.get(), props("etrium_plateblock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ETRIUM_PANEL = ITEMS.register("etrium_panel", () -> new ModBlockItem(ModBlocks.ETRIUM_PANEL.get(), props("etrium_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ETRIUM_BLOCK = ITEMS.register("etrium_block", () -> new ModBlockItem(ModBlocks.ETRIUM_BLOCK.get(), props("etrium_block").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> STEEL_FACTORY_BLOCK = ITEMS.register("steel_factory_block", () -> new ModBlockItem(ModBlocks.STEEL_FACTORY_BLOCK.get(), props("steel_factory_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENCASED_STEEL_BLOCK = ITEMS.register("encased_steel_block", () -> new ModBlockItem(ModBlocks.ENCASED_STEEL_BLOCK.get(), props("encased_steel_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PLATEBLOCK = ITEMS.register("steel_plateblock", () -> new ModBlockItem(ModBlocks.STEEL_PLATEBLOCK.get(), props("steel_plateblock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PANEL = ITEMS.register("steel_panel", () -> new ModBlockItem(ModBlocks.STEEL_PANEL.get(), props("steel_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_BLOCK = ITEMS.register("steel_block", () -> new ModBlockItem(ModBlocks.STEEL_BLOCK.get(), props("steel_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PLATING = ITEMS.register("steel_plating", () -> new ModBlockItem(ModBlocks.STEEL_PLATING.get(), props("steel_plating").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PLATING_STAIRS = ITEMS.register("steel_plating_stairs", () -> new ModBlockItem(ModBlocks.STEEL_PLATING_STAIRS.get(), props("steel_plating_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PLATING_SLAB = ITEMS.register("steel_plating_slab", () -> new ModBlockItem(ModBlocks.STEEL_PLATING_SLAB.get(), props("steel_plating_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PILLAR = ITEMS.register("steel_pillar", () -> new ModBlockItem(ModBlocks.STEEL_PILLAR.get(), props("steel_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLOWING_STEEL_PILLAR = ITEMS.register("glowing_steel_pillar", () -> new ModBlockItem(ModBlocks.GLOWING_STEEL_PILLAR.get(), props("glowing_steel_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PLATING_BUTTON = ITEMS.register("steel_plating_button", () -> new ModBlockItem(ModBlocks.STEEL_PLATING_BUTTON.get(), props("steel_plating_button").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_PLATING_PRESSURE_PLATE = ITEMS.register("steel_plating_pressure_plate", () -> new ModBlockItem(ModBlocks.STEEL_PLATING_PRESSURE_PLATE.get(), props("steel_plating_pressure_plate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_SLIDING_DOOR = SLIDING_DOORS.register("steel_sliding_door", () -> new ModBlockItem(ModBlocks.STEEL_SLIDING_DOOR.get(), props("steel_sliding_door").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AIRLOCK = SLIDING_DOORS.register("airlock", () -> new ModBlockItem(ModBlocks.AIRLOCK.get(), props("airlock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> REINFORCED_DOOR = SLIDING_DOORS.register("reinforced_door", () -> new ModBlockItem(ModBlocks.REINFORCED_DOOR.get(), props("reinforced_door").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_DOOR = BASIC_ITEMS.register("steel_door", () -> new ModBlockItem(ModBlocks.STEEL_DOOR.get(), props("steel_door").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STEEL_TRAPDOOR = ITEMS.register("steel_trapdoor", () -> new ModBlockItem(ModBlocks.STEEL_TRAPDOOR.get(), props("steel_trapdoor").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> DESH_FACTORY_BLOCK = ITEMS.register("desh_factory_block", () -> new ModBlockItem(ModBlocks.DESH_FACTORY_BLOCK.get(), props("desh_factory_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENCASED_DESH_BLOCK = ITEMS.register("encased_desh_block", () -> new ModBlockItem(ModBlocks.ENCASED_DESH_BLOCK.get(), props("encased_desh_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PLATEBLOCK = ITEMS.register("desh_plateblock", () -> new ModBlockItem(ModBlocks.DESH_PLATEBLOCK.get(), props("desh_plateblock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PANEL = ITEMS.register("desh_panel", () -> new ModBlockItem(ModBlocks.DESH_PANEL.get(), props("desh_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_BLOCK = ITEMS.register("desh_block", () -> new ModBlockItem(ModBlocks.DESH_BLOCK.get(), props("desh_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> RAW_DESH_BLOCK = ITEMS.register("raw_desh_block", () -> new ModBlockItem(ModBlocks.RAW_DESH_BLOCK.get(), props("raw_desh_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PLATING = ITEMS.register("desh_plating", () -> new ModBlockItem(ModBlocks.DESH_PLATING.get(), props("desh_plating").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PLATING_STAIRS = ITEMS.register("desh_plating_stairs", () -> new ModBlockItem(ModBlocks.DESH_PLATING_STAIRS.get(), props("desh_plating_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PLATING_SLAB = ITEMS.register("desh_plating_slab", () -> new ModBlockItem(ModBlocks.DESH_PLATING_SLAB.get(), props("desh_plating_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PILLAR = ITEMS.register("desh_pillar", () -> new ModBlockItem(ModBlocks.DESH_PILLAR.get(), props("desh_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLOWING_DESH_PILLAR = ITEMS.register("glowing_desh_pillar", () -> new ModBlockItem(ModBlocks.GLOWING_DESH_PILLAR.get(), props("glowing_desh_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PLATING_BUTTON = ITEMS.register("desh_plating_button", () -> new ModBlockItem(ModBlocks.DESH_PLATING_BUTTON.get(), props("desh_plating_button").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_PLATING_PRESSURE_PLATE = ITEMS.register("desh_plating_pressure_plate", () -> new ModBlockItem(ModBlocks.DESH_PLATING_PRESSURE_PLATE.get(), props("desh_plating_pressure_plate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DESH_SLIDING_DOOR = SLIDING_DOORS.register("desh_sliding_door", () -> new ModBlockItem(ModBlocks.DESH_SLIDING_DOOR.get(), props("desh_sliding_door").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> OSTRUM_FACTORY_BLOCK = ITEMS.register("ostrum_factory_block", () -> new ModBlockItem(ModBlocks.OSTRUM_FACTORY_BLOCK.get(), props("ostrum_factory_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENCASED_OSTRUM_BLOCK = ITEMS.register("encased_ostrum_block", () -> new ModBlockItem(ModBlocks.ENCASED_OSTRUM_BLOCK.get(), props("encased_ostrum_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PLATEBLOCK = ITEMS.register("ostrum_plateblock", () -> new ModBlockItem(ModBlocks.OSTRUM_PLATEBLOCK.get(), props("ostrum_plateblock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PANEL = ITEMS.register("ostrum_panel", () -> new ModBlockItem(ModBlocks.OSTRUM_PANEL.get(), props("ostrum_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_BLOCK = ITEMS.register("ostrum_block", () -> new ModBlockItem(ModBlocks.OSTRUM_BLOCK.get(), props("ostrum_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> RAW_OSTRUM_BLOCK = ITEMS.register("raw_ostrum_block", () -> new ModBlockItem(ModBlocks.RAW_OSTRUM_BLOCK.get(), props("raw_ostrum_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PLATING = ITEMS.register("ostrum_plating", () -> new ModBlockItem(ModBlocks.OSTRUM_PLATING.get(), props("ostrum_plating").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PLATING_STAIRS = ITEMS.register("ostrum_plating_stairs", () -> new ModBlockItem(ModBlocks.OSTRUM_PLATING_STAIRS.get(), props("ostrum_plating_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PLATING_SLAB = ITEMS.register("ostrum_plating_slab", () -> new ModBlockItem(ModBlocks.OSTRUM_PLATING_SLAB.get(), props("ostrum_plating_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PILLAR = ITEMS.register("ostrum_pillar", () -> new ModBlockItem(ModBlocks.OSTRUM_PILLAR.get(), props("ostrum_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLOWING_OSTRUM_PILLAR = ITEMS.register("glowing_ostrum_pillar", () -> new ModBlockItem(ModBlocks.GLOWING_OSTRUM_PILLAR.get(), props("glowing_ostrum_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PLATING_BUTTON = ITEMS.register("ostrum_plating_button", () -> new ModBlockItem(ModBlocks.OSTRUM_PLATING_BUTTON.get(), props("ostrum_plating_button").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_PLATING_PRESSURE_PLATE = ITEMS.register("ostrum_plating_pressure_plate", () -> new ModBlockItem(ModBlocks.OSTRUM_PLATING_PRESSURE_PLATE.get(), props("ostrum_plating_pressure_plate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> OSTRUM_SLIDING_DOOR = SLIDING_DOORS.register("ostrum_sliding_door", () -> new ModBlockItem(ModBlocks.OSTRUM_SLIDING_DOOR.get(), props("ostrum_sliding_door").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> CALORITE_FACTORY_BLOCK = ITEMS.register("calorite_factory_block", () -> new ModBlockItem(ModBlocks.CALORITE_FACTORY_BLOCK.get(), props("calorite_factory_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ENCASED_CALORITE_BLOCK = ITEMS.register("encased_calorite_block", () -> new ModBlockItem(ModBlocks.ENCASED_CALORITE_BLOCK.get(), props("encased_calorite_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PLATEBLOCK = ITEMS.register("calorite_plateblock", () -> new ModBlockItem(ModBlocks.CALORITE_PLATEBLOCK.get(), props("calorite_plateblock").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PANEL = ITEMS.register("calorite_panel", () -> new ModBlockItem(ModBlocks.CALORITE_PANEL.get(), props("calorite_panel").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_BLOCK = ITEMS.register("calorite_block", () -> new ModBlockItem(ModBlocks.CALORITE_BLOCK.get(), props("calorite_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> RAW_CALORITE_BLOCK = ITEMS.register("raw_calorite_block", () -> new ModBlockItem(ModBlocks.RAW_CALORITE_BLOCK.get(), props("raw_calorite_block").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PLATING = ITEMS.register("calorite_plating", () -> new ModBlockItem(ModBlocks.CALORITE_PLATING.get(), props("calorite_plating").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PLATING_STAIRS = ITEMS.register("calorite_plating_stairs", () -> new ModBlockItem(ModBlocks.CALORITE_PLATING_STAIRS.get(), props("calorite_plating_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PLATING_SLAB = ITEMS.register("calorite_plating_slab", () -> new ModBlockItem(ModBlocks.CALORITE_PLATING_SLAB.get(), props("calorite_plating_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PILLAR = ITEMS.register("calorite_pillar", () -> new ModBlockItem(ModBlocks.CALORITE_PILLAR.get(), props("calorite_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLOWING_CALORITE_PILLAR = ITEMS.register("glowing_calorite_pillar", () -> new ModBlockItem(ModBlocks.GLOWING_CALORITE_PILLAR.get(), props("glowing_calorite_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PLATING_BUTTON = ITEMS.register("calorite_plating_button", () -> new ModBlockItem(ModBlocks.CALORITE_PLATING_BUTTON.get(), props("calorite_plating_button").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_PLATING_PRESSURE_PLATE = ITEMS.register("calorite_plating_pressure_plate", () -> new ModBlockItem(ModBlocks.CALORITE_PLATING_PRESSURE_PLATE.get(), props("calorite_plating_pressure_plate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CALORITE_SLIDING_DOOR = SLIDING_DOORS.register("calorite_sliding_door", () -> new ModBlockItem(ModBlocks.CALORITE_SLIDING_DOOR.get(), props("calorite_sliding_door").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> BLACK_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("black_industrial_lamp", () -> new ModBlockItem(ModBlocks.BLACK_INDUSTRIAL_LAMP.get(), props("black_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> BLUE_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("blue_industrial_lamp", () -> new ModBlockItem(ModBlocks.BLUE_INDUSTRIAL_LAMP.get(), props("blue_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> BROWN_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("brown_industrial_lamp", () -> new ModBlockItem(ModBlocks.BROWN_INDUSTRIAL_LAMP.get(), props("brown_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CYAN_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("cyan_industrial_lamp", () -> new ModBlockItem(ModBlocks.CYAN_INDUSTRIAL_LAMP.get(), props("cyan_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GRAY_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("gray_industrial_lamp", () -> new ModBlockItem(ModBlocks.GRAY_INDUSTRIAL_LAMP.get(), props("gray_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GREEN_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("green_industrial_lamp", () -> new ModBlockItem(ModBlocks.GREEN_INDUSTRIAL_LAMP.get(), props("green_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> LIGHT_BLUE_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("light_blue_industrial_lamp", () -> new ModBlockItem(ModBlocks.LIGHT_BLUE_INDUSTRIAL_LAMP.get(), props("light_blue_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> LIGHT_GRAY_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("light_gray_industrial_lamp", () -> new ModBlockItem(ModBlocks.LIGHT_GRAY_INDUSTRIAL_LAMP.get(), props("light_gray_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> LIME_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("lime_industrial_lamp", () -> new ModBlockItem(ModBlocks.LIME_INDUSTRIAL_LAMP.get(), props("lime_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MAGENTA_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("magenta_industrial_lamp", () -> new ModBlockItem(ModBlocks.MAGENTA_INDUSTRIAL_LAMP.get(), props("magenta_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> ORANGE_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("orange_industrial_lamp", () -> new ModBlockItem(ModBlocks.ORANGE_INDUSTRIAL_LAMP.get(), props("orange_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PINK_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("pink_industrial_lamp", () -> new ModBlockItem(ModBlocks.PINK_INDUSTRIAL_LAMP.get(), props("pink_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PURPLE_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("purple_industrial_lamp", () -> new ModBlockItem(ModBlocks.PURPLE_INDUSTRIAL_LAMP.get(), props("purple_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> RED_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("red_industrial_lamp", () -> new ModBlockItem(ModBlocks.RED_INDUSTRIAL_LAMP.get(), props("red_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> WHITE_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("white_industrial_lamp", () -> new ModBlockItem(ModBlocks.WHITE_INDUSTRIAL_LAMP.get(), props("white_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> YELLOW_INDUSTRIAL_LAMP = INDUSTRIAL_LAMPS.register("yellow_industrial_lamp", () -> new ModBlockItem(ModBlocks.YELLOW_INDUSTRIAL_LAMP.get(), props("yellow_industrial_lamp").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> SMALL_BLACK_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_black_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_BLACK_INDUSTRIAL_LAMP.get(), props("small_black_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_BLUE_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_blue_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_BLUE_INDUSTRIAL_LAMP.get(), props("small_blue_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_BROWN_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_brown_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_BROWN_INDUSTRIAL_LAMP.get(), props("small_brown_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_CYAN_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_cyan_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_CYAN_INDUSTRIAL_LAMP.get(), props("small_cyan_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_GRAY_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_gray_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_GRAY_INDUSTRIAL_LAMP.get(), props("small_gray_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_GREEN_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_green_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_GREEN_INDUSTRIAL_LAMP.get(), props("small_green_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_LIGHT_BLUE_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_light_blue_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_LIGHT_BLUE_INDUSTRIAL_LAMP.get(), props("small_light_blue_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_LIGHT_GRAY_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_light_gray_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_LIGHT_GRAY_INDUSTRIAL_LAMP.get(), props("small_light_gray_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_LIME_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_lime_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_LIME_INDUSTRIAL_LAMP.get(), props("small_lime_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_MAGENTA_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_magenta_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_MAGENTA_INDUSTRIAL_LAMP.get(), props("small_magenta_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_ORANGE_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_orange_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_ORANGE_INDUSTRIAL_LAMP.get(), props("small_orange_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_PINK_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_pink_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_PINK_INDUSTRIAL_LAMP.get(), props("small_pink_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_PURPLE_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_purple_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_PURPLE_INDUSTRIAL_LAMP.get(), props("small_purple_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_RED_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_red_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_RED_INDUSTRIAL_LAMP.get(), props("small_red_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_WHITE_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_white_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_WHITE_INDUSTRIAL_LAMP.get(), props("small_white_industrial_lamp").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> SMALL_YELLOW_INDUSTRIAL_LAMP = SMALL_INDUSTRIAL_LAMPS.register("small_yellow_industrial_lamp", () -> new ModBlockItem(ModBlocks.SMALL_YELLOW_INDUSTRIAL_LAMP.get(), props("small_yellow_industrial_lamp").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> MOON_SAND = ITEMS.register("moon_sand", () -> new ModBlockItem(ModBlocks.MOON_SAND.get(), props("moon_sand").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE = ITEMS.register("moon_stone", () -> new ModBlockItem(ModBlocks.MOON_STONE.get(), props("moon_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_DEEPSLATE = ITEMS.register("moon_deepslate", () -> new ModBlockItem(ModBlocks.MOON_DEEPSLATE.get(), props("moon_deepslate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE_STAIRS = ITEMS.register("moon_stone_stairs", () -> new ModBlockItem(ModBlocks.MOON_STONE_STAIRS.get(), props("moon_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE_SLAB = ITEMS.register("moon_stone_slab", () -> new ModBlockItem(ModBlocks.MOON_STONE_SLAB.get(), props("moon_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_COBBLESTONE = ITEMS.register("moon_cobblestone", () -> new ModBlockItem(ModBlocks.MOON_COBBLESTONE.get(), props("moon_cobblestone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_COBBLESTONE_STAIRS = ITEMS.register("moon_cobblestone_stairs", () -> new ModBlockItem(ModBlocks.MOON_COBBLESTONE_STAIRS.get(), props("moon_cobblestone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_COBBLESTONE_SLAB = ITEMS.register("moon_cobblestone_slab", () -> new ModBlockItem(ModBlocks.MOON_COBBLESTONE_SLAB.get(), props("moon_cobblestone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE_BRICKS = ITEMS.register("moon_stone_bricks", () -> new ModBlockItem(ModBlocks.MOON_STONE_BRICKS.get(), props("moon_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE_BRICK_SLAB = ITEMS.register("moon_stone_brick_slab", () -> new ModBlockItem(ModBlocks.MOON_STONE_BRICK_SLAB.get(), props("moon_stone_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE_BRICK_STAIRS = ITEMS.register("moon_stone_brick_stairs", () -> new ModBlockItem(ModBlocks.MOON_STONE_BRICK_STAIRS.get(), props("moon_stone_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_MOON_STONE_BRICKS = ITEMS.register("cracked_moon_stone_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_MOON_STONE_BRICKS.get(), props("cracked_moon_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MOON_STONE_BRICKS = ITEMS.register("chiseled_moon_stone_bricks", () -> new ModBlockItem(ModBlocks.CHISELED_MOON_STONE_BRICKS.get(), props("chiseled_moon_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MOON_STONE_STAIRS = ITEMS.register("chiseled_moon_stone_stairs", () -> new ModBlockItem(ModBlocks.CHISELED_MOON_STONE_STAIRS.get(), props("chiseled_moon_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MOON_STONE_SLAB = ITEMS.register("chiseled_moon_stone_slab", () -> new ModBlockItem(ModBlocks.CHISELED_MOON_STONE_SLAB.get(), props("chiseled_moon_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MOON_STONE = ITEMS.register("polished_moon_stone", () -> new ModBlockItem(ModBlocks.POLISHED_MOON_STONE.get(), props("polished_moon_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MOON_STONE_STAIRS = ITEMS.register("polished_moon_stone_stairs", () -> new ModBlockItem(ModBlocks.POLISHED_MOON_STONE_STAIRS.get(), props("polished_moon_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MOON_STONE_SLAB = ITEMS.register("polished_moon_stone_slab", () -> new ModBlockItem(ModBlocks.POLISHED_MOON_STONE_SLAB.get(), props("polished_moon_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_PILLAR = ITEMS.register("moon_pillar", () -> new ModBlockItem(ModBlocks.MOON_PILLAR.get(), props("moon_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_STONE_BRICK_WALL = ITEMS.register("moon_stone_brick_wall", () -> new ModBlockItem(ModBlocks.MOON_STONE_BRICK_WALL.get(), props("moon_stone_brick_wall").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> MOON_CHEESE_ORE = ITEMS.register("moon_cheese_ore", () -> new ModBlockItem(ModBlocks.MOON_CHEESE_ORE.get(), props("moon_cheese_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_DESH_ORE = ITEMS.register("moon_desh_ore", () -> new ModBlockItem(ModBlocks.MOON_DESH_ORE.get(), props("moon_desh_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DEEPSLATE_DESH_ORE = ITEMS.register("deepslate_desh_ore", () -> new ModBlockItem(ModBlocks.DEEPSLATE_DESH_ORE.get(), props("deepslate_desh_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_IRON_ORE = ITEMS.register("moon_iron_ore", () -> new ModBlockItem(ModBlocks.MOON_IRON_ORE.get(), props("moon_iron_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MOON_ICE_SHARD_ORE = ITEMS.register("moon_ice_shard_ore", () -> new ModBlockItem(ModBlocks.MOON_ICE_SHARD_ORE.get(), props("moon_ice_shard_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DEEPSLATE_ICE_SHARD_ORE = ITEMS.register("deepslate_ice_shard_ore", () -> new ModBlockItem(ModBlocks.DEEPSLATE_ICE_SHARD_ORE.get(), props("deepslate_ice_shard_ore").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> AERONOS_CAP = ITEMS.register("aeronos_cap", () -> new ModBlockItem(ModBlocks.AERONOS_CAP.get(), props("aeronos_cap").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_STEM = ITEMS.register("aeronos_stem", () -> new ModBlockItem(ModBlocks.AERONOS_STEM.get(), props("aeronos_stem").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_PLANKS = ITEMS.register("aeronos_planks", () -> new ModBlockItem(ModBlocks.AERONOS_PLANKS.get(), props("aeronos_planks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_STAIRS = ITEMS.register("aeronos_stairs", () -> new ModBlockItem(ModBlocks.AERONOS_STAIRS.get(), props("aeronos_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_SLAB = ITEMS.register("aeronos_slab", () -> new ModBlockItem(ModBlocks.AERONOS_SLAB.get(), props("aeronos_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_FENCE = ITEMS.register("aeronos_fence", () -> new ModBlockItem(ModBlocks.AERONOS_FENCE.get(), props("aeronos_fence").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_FENCE_GATE = ITEMS.register("aeronos_fence_gate", () -> new ModBlockItem(ModBlocks.AERONOS_FENCE_GATE.get(), props("aeronos_fence_gate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_LADDER = ITEMS.register("aeronos_ladder", () -> new ModBlockItem(ModBlocks.AERONOS_LADDER.get(), props("aeronos_ladder").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_DOOR = BASIC_ITEMS.register("aeronos_door", () -> new ModBlockItem(ModBlocks.AERONOS_DOOR.get(), props("aeronos_door").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> AERONOS_TRAPDOOR = ITEMS.register("aeronos_trapdoor", () -> new ModBlockItem(ModBlocks.AERONOS_TRAPDOOR.get(), props("aeronos_trapdoor").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> STROPHAR_CAP = ITEMS.register("strophar_cap", () -> new ModBlockItem(ModBlocks.STROPHAR_CAP.get(), props("strophar_cap").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_STEM = ITEMS.register("strophar_stem", () -> new ModBlockItem(ModBlocks.STROPHAR_STEM.get(), props("strophar_stem").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_PLANKS = ITEMS.register("strophar_planks", () -> new ModBlockItem(ModBlocks.STROPHAR_PLANKS.get(), props("strophar_planks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_STAIRS = ITEMS.register("strophar_stairs", () -> new ModBlockItem(ModBlocks.STROPHAR_STAIRS.get(), props("strophar_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_SLAB = ITEMS.register("strophar_slab", () -> new ModBlockItem(ModBlocks.STROPHAR_SLAB.get(), props("strophar_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_FENCE = ITEMS.register("strophar_fence", () -> new ModBlockItem(ModBlocks.STROPHAR_FENCE.get(), props("strophar_fence").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_FENCE_GATE = ITEMS.register("strophar_fence_gate", () -> new ModBlockItem(ModBlocks.STROPHAR_FENCE_GATE.get(), props("strophar_fence_gate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_LADDER = ITEMS.register("strophar_ladder", () -> new ModBlockItem(ModBlocks.STROPHAR_LADDER.get(), props("strophar_ladder").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_DOOR = BASIC_ITEMS.register("strophar_door", () -> new ModBlockItem(ModBlocks.STROPHAR_DOOR.get(), props("strophar_door").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STROPHAR_TRAPDOOR = ITEMS.register("strophar_trapdoor", () -> new ModBlockItem(ModBlocks.STROPHAR_TRAPDOOR.get(), props("strophar_trapdoor").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> MARS_SAND = ITEMS.register("mars_sand", () -> new ModBlockItem(ModBlocks.MARS_SAND.get(), props("mars_sand").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE = ITEMS.register("mars_stone", () -> new ModBlockItem(ModBlocks.MARS_STONE.get(), props("mars_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE_STAIRS = ITEMS.register("mars_stone_stairs", () -> new ModBlockItem(ModBlocks.MARS_STONE_STAIRS.get(), props("mars_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE_SLAB = ITEMS.register("mars_stone_slab", () -> new ModBlockItem(ModBlocks.MARS_STONE_SLAB.get(), props("mars_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_COBBLESTONE = ITEMS.register("mars_cobblestone", () -> new ModBlockItem(ModBlocks.MARS_COBBLESTONE.get(), props("mars_cobblestone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_COBBLESTONE_STAIRS = ITEMS.register("mars_cobblestone_stairs", () -> new ModBlockItem(ModBlocks.MARS_COBBLESTONE_STAIRS.get(), props("mars_cobblestone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_COBBLESTONE_SLAB = ITEMS.register("mars_cobblestone_slab", () -> new ModBlockItem(ModBlocks.MARS_COBBLESTONE_SLAB.get(), props("mars_cobblestone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE_BRICKS = ITEMS.register("mars_stone_bricks", () -> new ModBlockItem(ModBlocks.MARS_STONE_BRICKS.get(), props("mars_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE_BRICK_SLAB = ITEMS.register("mars_stone_brick_slab", () -> new ModBlockItem(ModBlocks.MARS_STONE_BRICK_SLAB.get(), props("mars_stone_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE_BRICK_STAIRS = ITEMS.register("mars_stone_brick_stairs", () -> new ModBlockItem(ModBlocks.MARS_STONE_BRICK_STAIRS.get(), props("mars_stone_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_MARS_STONE_BRICKS = ITEMS.register("cracked_mars_stone_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_MARS_STONE_BRICKS.get(), props("cracked_mars_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MARS_STONE_BRICKS = ITEMS.register("chiseled_mars_stone_bricks", () -> new ModBlockItem(ModBlocks.CHISELED_MARS_STONE_BRICKS.get(), props("chiseled_mars_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MARS_STONE_STAIRS = ITEMS.register("chiseled_mars_stone_stairs", () -> new ModBlockItem(ModBlocks.CHISELED_MARS_STONE_STAIRS.get(), props("chiseled_mars_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MARS_STONE_SLAB = ITEMS.register("chiseled_mars_stone_slab", () -> new ModBlockItem(ModBlocks.CHISELED_MARS_STONE_SLAB.get(), props("chiseled_mars_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MARS_STONE = ITEMS.register("polished_mars_stone", () -> new ModBlockItem(ModBlocks.POLISHED_MARS_STONE.get(), props("polished_mars_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MARS_STONE_STAIRS = ITEMS.register("polished_mars_stone_stairs", () -> new ModBlockItem(ModBlocks.POLISHED_MARS_STONE_STAIRS.get(), props("polished_mars_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MARS_STONE_SLAB = ITEMS.register("polished_mars_stone_slab", () -> new ModBlockItem(ModBlocks.POLISHED_MARS_STONE_SLAB.get(), props("polished_mars_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_PILLAR = ITEMS.register("mars_pillar", () -> new ModBlockItem(ModBlocks.MARS_PILLAR.get(), props("mars_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_STONE_BRICK_WALL = ITEMS.register("mars_stone_brick_wall", () -> new ModBlockItem(ModBlocks.MARS_STONE_BRICK_WALL.get(), props("mars_stone_brick_wall").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> CONGLOMERATE = ITEMS.register("conglomerate", () -> new ModBlockItem(ModBlocks.CONGLOMERATE.get(), props("conglomerate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_CONGLOMERATE = ITEMS.register("polished_conglomerate", () -> new ModBlockItem(ModBlocks.POLISHED_CONGLOMERATE.get(), props("polished_conglomerate").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> MARS_IRON_ORE = ITEMS.register("mars_iron_ore", () -> new ModBlockItem(ModBlocks.MARS_IRON_ORE.get(), props("mars_iron_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_DIAMOND_ORE = ITEMS.register("mars_diamond_ore", () -> new ModBlockItem(ModBlocks.MARS_DIAMOND_ORE.get(), props("mars_diamond_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_OSTRUM_ORE = ITEMS.register("mars_ostrum_ore", () -> new ModBlockItem(ModBlocks.MARS_OSTRUM_ORE.get(), props("mars_ostrum_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DEEPSLATE_OSTRUM_ORE = ITEMS.register("deepslate_ostrum_ore", () -> new ModBlockItem(ModBlocks.DEEPSLATE_OSTRUM_ORE.get(), props("deepslate_ostrum_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MARS_ICE_SHARD_ORE = ITEMS.register("mars_ice_shard_ore", () -> new ModBlockItem(ModBlocks.MARS_ICE_SHARD_ORE.get(), props("mars_ice_shard_ore").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> VENUS_SANDSTONE = ITEMS.register("venus_sandstone", () -> new ModBlockItem(ModBlocks.VENUS_SANDSTONE.get(), props("venus_sandstone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_SANDSTONE_BRICKS = ITEMS.register("venus_sandstone_bricks", () -> new ModBlockItem(ModBlocks.VENUS_SANDSTONE_BRICKS.get(), props("venus_sandstone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_SANDSTONE_BRICK_SLAB = ITEMS.register("venus_sandstone_brick_slab", () -> new ModBlockItem(ModBlocks.VENUS_SANDSTONE_BRICK_SLAB.get(), props("venus_sandstone_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_SANDSTONE_BRICK_STAIRS = ITEMS.register("venus_sandstone_brick_stairs", () -> new ModBlockItem(ModBlocks.VENUS_SANDSTONE_BRICK_STAIRS.get(), props("venus_sandstone_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_VENUS_SANDSTONE_BRICKS = ITEMS.register("cracked_venus_sandstone_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_VENUS_SANDSTONE_BRICKS.get(), props("cracked_venus_sandstone_bricks").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> VENUS_SAND = ITEMS.register("venus_sand", () -> new ModBlockItem(ModBlocks.VENUS_SAND.get(), props("venus_sand").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE = ITEMS.register("venus_stone", () -> new ModBlockItem(ModBlocks.VENUS_STONE.get(), props("venus_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE_STAIRS = ITEMS.register("venus_stone_stairs", () -> new ModBlockItem(ModBlocks.VENUS_STONE_STAIRS.get(), props("venus_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE_SLAB = ITEMS.register("venus_stone_slab", () -> new ModBlockItem(ModBlocks.VENUS_STONE_SLAB.get(), props("venus_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_COBBLESTONE = ITEMS.register("venus_cobblestone", () -> new ModBlockItem(ModBlocks.VENUS_COBBLESTONE.get(), props("venus_cobblestone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_COBBLESTONE_STAIRS = ITEMS.register("venus_cobblestone_stairs", () -> new ModBlockItem(ModBlocks.VENUS_COBBLESTONE_STAIRS.get(), props("venus_cobblestone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_COBBLESTONE_SLAB = ITEMS.register("venus_cobblestone_slab", () -> new ModBlockItem(ModBlocks.VENUS_COBBLESTONE_SLAB.get(), props("venus_cobblestone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE_BRICKS = ITEMS.register("venus_stone_bricks", () -> new ModBlockItem(ModBlocks.VENUS_STONE_BRICKS.get(), props("venus_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE_BRICK_SLAB = ITEMS.register("venus_stone_brick_slab", () -> new ModBlockItem(ModBlocks.VENUS_STONE_BRICK_SLAB.get(), props("venus_stone_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE_BRICK_STAIRS = ITEMS.register("venus_stone_brick_stairs", () -> new ModBlockItem(ModBlocks.VENUS_STONE_BRICK_STAIRS.get(), props("venus_stone_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_VENUS_STONE_BRICKS = ITEMS.register("cracked_venus_stone_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_VENUS_STONE_BRICKS.get(), props("cracked_venus_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_VENUS_STONE_BRICKS = ITEMS.register("chiseled_venus_stone_bricks", () -> new ModBlockItem(ModBlocks.CHISELED_VENUS_STONE_BRICKS.get(), props("chiseled_venus_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_VENUS_STONE_STAIRS = ITEMS.register("chiseled_venus_stone_stairs", () -> new ModBlockItem(ModBlocks.CHISELED_VENUS_STONE_STAIRS.get(), props("chiseled_venus_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_VENUS_STONE_SLAB = ITEMS.register("chiseled_venus_stone_slab", () -> new ModBlockItem(ModBlocks.CHISELED_VENUS_STONE_SLAB.get(), props("chiseled_venus_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_VENUS_STONE = ITEMS.register("polished_venus_stone", () -> new ModBlockItem(ModBlocks.POLISHED_VENUS_STONE.get(), props("polished_venus_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_VENUS_STONE_STAIRS = ITEMS.register("polished_venus_stone_stairs", () -> new ModBlockItem(ModBlocks.POLISHED_VENUS_STONE_STAIRS.get(), props("polished_venus_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_VENUS_STONE_SLAB = ITEMS.register("polished_venus_stone_slab", () -> new ModBlockItem(ModBlocks.POLISHED_VENUS_STONE_SLAB.get(), props("polished_venus_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_PILLAR = ITEMS.register("venus_pillar", () -> new ModBlockItem(ModBlocks.VENUS_PILLAR.get(), props("venus_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_STONE_BRICK_WALL = ITEMS.register("venus_stone_brick_wall", () -> new ModBlockItem(ModBlocks.VENUS_STONE_BRICK_WALL.get(), props("venus_stone_brick_wall").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> VENUS_COAL_ORE = ITEMS.register("venus_coal_ore", () -> new ModBlockItem(ModBlocks.VENUS_COAL_ORE.get(), props("venus_coal_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_GOLD_ORE = ITEMS.register("venus_gold_ore", () -> new ModBlockItem(ModBlocks.VENUS_GOLD_ORE.get(), props("venus_gold_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_DIAMOND_ORE = ITEMS.register("venus_diamond_ore", () -> new ModBlockItem(ModBlocks.VENUS_DIAMOND_ORE.get(), props("venus_diamond_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> VENUS_CALORITE_ORE = ITEMS.register("venus_calorite_ore", () -> new ModBlockItem(ModBlocks.VENUS_CALORITE_ORE.get(), props("venus_calorite_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> DEEPSLATE_CALORITE_ORE = ITEMS.register("deepslate_calorite_ore", () -> new ModBlockItem(ModBlocks.DEEPSLATE_CALORITE_ORE.get(), props("deepslate_calorite_ore").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> INFERNAL_SPIRE_BLOCK = ITEMS.register("infernal_spire_block", () -> new ModBlockItem(ModBlocks.INFERNAL_SPIRE_BLOCK.get(), props("infernal_spire_block").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> MERCURY_STONE = ITEMS.register("mercury_stone", () -> new ModBlockItem(ModBlocks.MERCURY_STONE.get(), props("mercury_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_STONE_STAIRS = ITEMS.register("mercury_stone_stairs", () -> new ModBlockItem(ModBlocks.MERCURY_STONE_STAIRS.get(), props("mercury_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_STONE_SLAB = ITEMS.register("mercury_stone_slab", () -> new ModBlockItem(ModBlocks.MERCURY_STONE_SLAB.get(), props("mercury_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_COBBLESTONE = ITEMS.register("mercury_cobblestone", () -> new ModBlockItem(ModBlocks.MERCURY_COBBLESTONE.get(), props("mercury_cobblestone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_COBBLESTONE_STAIRS = ITEMS.register("mercury_cobblestone_stairs", () -> new ModBlockItem(ModBlocks.MERCURY_COBBLESTONE_STAIRS.get(), props("mercury_cobblestone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_COBBLESTONE_SLAB = ITEMS.register("mercury_cobblestone_slab", () -> new ModBlockItem(ModBlocks.MERCURY_COBBLESTONE_SLAB.get(), props("mercury_cobblestone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_STONE_BRICKS = ITEMS.register("mercury_stone_bricks", () -> new ModBlockItem(ModBlocks.MERCURY_STONE_BRICKS.get(), props("mercury_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_STONE_BRICK_SLAB = ITEMS.register("mercury_stone_brick_slab", () -> new ModBlockItem(ModBlocks.MERCURY_STONE_BRICK_SLAB.get(), props("mercury_stone_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_STONE_BRICK_STAIRS = ITEMS.register("mercury_stone_brick_stairs", () -> new ModBlockItem(ModBlocks.MERCURY_STONE_BRICK_STAIRS.get(), props("mercury_stone_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_MERCURY_STONE_BRICKS = ITEMS.register("cracked_mercury_stone_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_MERCURY_STONE_BRICKS.get(), props("cracked_mercury_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MERCURY_STONE_BRICKS = ITEMS.register("chiseled_mercury_stone_bricks", () -> new ModBlockItem(ModBlocks.CHISELED_MERCURY_STONE_BRICKS.get(), props("chiseled_mercury_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MERCURY_STONE_STAIRS = ITEMS.register("chiseled_mercury_stone_stairs", () -> new ModBlockItem(ModBlocks.CHISELED_MERCURY_STONE_STAIRS.get(), props("chiseled_mercury_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_MERCURY_STONE_SLAB = ITEMS.register("chiseled_mercury_stone_slab", () -> new ModBlockItem(ModBlocks.CHISELED_MERCURY_STONE_SLAB.get(), props("chiseled_mercury_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MERCURY_STONE = ITEMS.register("polished_mercury_stone", () -> new ModBlockItem(ModBlocks.POLISHED_MERCURY_STONE.get(), props("polished_mercury_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MERCURY_STONE_STAIRS = ITEMS.register("polished_mercury_stone_stairs", () -> new ModBlockItem(ModBlocks.POLISHED_MERCURY_STONE_STAIRS.get(), props("polished_mercury_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_MERCURY_STONE_SLAB = ITEMS.register("polished_mercury_stone_slab", () -> new ModBlockItem(ModBlocks.POLISHED_MERCURY_STONE_SLAB.get(), props("polished_mercury_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_PILLAR = ITEMS.register("mercury_pillar", () -> new ModBlockItem(ModBlocks.MERCURY_PILLAR.get(), props("mercury_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> MERCURY_STONE_BRICK_WALL = ITEMS.register("mercury_stone_brick_wall", () -> new ModBlockItem(ModBlocks.MERCURY_STONE_BRICK_WALL.get(), props("mercury_stone_brick_wall").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> MERCURY_IRON_ORE = ITEMS.register("mercury_iron_ore", () -> new ModBlockItem(ModBlocks.MERCURY_IRON_ORE.get(), props("mercury_iron_ore").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> GLACIO_STONE = ITEMS.register("glacio_stone", () -> new ModBlockItem(ModBlocks.GLACIO_STONE.get(), props("glacio_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_STONE_STAIRS = ITEMS.register("glacio_stone_stairs", () -> new ModBlockItem(ModBlocks.GLACIO_STONE_STAIRS.get(), props("glacio_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_STONE_SLAB = ITEMS.register("glacio_stone_slab", () -> new ModBlockItem(ModBlocks.GLACIO_STONE_SLAB.get(), props("glacio_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_COBBLESTONE = ITEMS.register("glacio_cobblestone", () -> new ModBlockItem(ModBlocks.GLACIO_COBBLESTONE.get(), props("glacio_cobblestone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_COBBLESTONE_STAIRS = ITEMS.register("glacio_cobblestone_stairs", () -> new ModBlockItem(ModBlocks.GLACIO_COBBLESTONE_STAIRS.get(), props("glacio_cobblestone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_COBBLESTONE_SLAB = ITEMS.register("glacio_cobblestone_slab", () -> new ModBlockItem(ModBlocks.GLACIO_COBBLESTONE_SLAB.get(), props("glacio_cobblestone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_STONE_BRICKS = ITEMS.register("glacio_stone_bricks", () -> new ModBlockItem(ModBlocks.GLACIO_STONE_BRICKS.get(), props("glacio_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_STONE_BRICK_SLAB = ITEMS.register("glacio_stone_brick_slab", () -> new ModBlockItem(ModBlocks.GLACIO_STONE_BRICK_SLAB.get(), props("glacio_stone_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_STONE_BRICK_STAIRS = ITEMS.register("glacio_stone_brick_stairs", () -> new ModBlockItem(ModBlocks.GLACIO_STONE_BRICK_STAIRS.get(), props("glacio_stone_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_GLACIO_STONE_BRICKS = ITEMS.register("cracked_glacio_stone_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_GLACIO_STONE_BRICKS.get(), props("cracked_glacio_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_GLACIO_STONE_BRICKS = ITEMS.register("chiseled_glacio_stone_bricks", () -> new ModBlockItem(ModBlocks.CHISELED_GLACIO_STONE_BRICKS.get(), props("chiseled_glacio_stone_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_GLACIO_STONE_STAIRS = ITEMS.register("chiseled_glacio_stone_stairs", () -> new ModBlockItem(ModBlocks.CHISELED_GLACIO_STONE_STAIRS.get(), props("chiseled_glacio_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_GLACIO_STONE_SLAB = ITEMS.register("chiseled_glacio_stone_slab", () -> new ModBlockItem(ModBlocks.CHISELED_GLACIO_STONE_SLAB.get(), props("chiseled_glacio_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_GLACIO_STONE = ITEMS.register("polished_glacio_stone", () -> new ModBlockItem(ModBlocks.POLISHED_GLACIO_STONE.get(), props("polished_glacio_stone").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_GLACIO_STONE_STAIRS = ITEMS.register("polished_glacio_stone_stairs", () -> new ModBlockItem(ModBlocks.POLISHED_GLACIO_STONE_STAIRS.get(), props("polished_glacio_stone_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_GLACIO_STONE_SLAB = ITEMS.register("polished_glacio_stone_slab", () -> new ModBlockItem(ModBlocks.POLISHED_GLACIO_STONE_SLAB.get(), props("polished_glacio_stone_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_PILLAR = ITEMS.register("glacio_pillar", () -> new ModBlockItem(ModBlocks.GLACIO_PILLAR.get(), props("glacio_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_STONE_BRICK_WALL = ITEMS.register("glacio_stone_brick_wall", () -> new ModBlockItem(ModBlocks.GLACIO_STONE_BRICK_WALL.get(), props("glacio_stone_brick_wall").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> GLACIO_ICE_SHARD_ORE = ITEMS.register("glacio_ice_shard_ore", () -> new ModBlockItem(ModBlocks.GLACIO_ICE_SHARD_ORE.get(), props("glacio_ice_shard_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_COAL_ORE = ITEMS.register("glacio_coal_ore", () -> new ModBlockItem(ModBlocks.GLACIO_COAL_ORE.get(), props("glacio_coal_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_COPPER_ORE = ITEMS.register("glacio_copper_ore", () -> new ModBlockItem(ModBlocks.GLACIO_COPPER_ORE.get(), props("glacio_copper_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_IRON_ORE = ITEMS.register("glacio_iron_ore", () -> new ModBlockItem(ModBlocks.GLACIO_IRON_ORE.get(), props("glacio_iron_ore").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIO_LAPIS_ORE = ITEMS.register("glacio_lapis_ore", () -> new ModBlockItem(ModBlocks.GLACIO_LAPIS_ORE.get(), props("glacio_lapis_ore").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> PERMAFROST = ITEMS.register("permafrost", () -> new ModBlockItem(ModBlocks.PERMAFROST.get(), props("permafrost").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PERMAFROST_BRICKS = ITEMS.register("permafrost_bricks", () -> new ModBlockItem(ModBlocks.PERMAFROST_BRICKS.get(), props("permafrost_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PERMAFROST_BRICK_STAIRS = ITEMS.register("permafrost_brick_stairs", () -> new ModBlockItem(ModBlocks.PERMAFROST_BRICK_STAIRS.get(), props("permafrost_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PERMAFROST_BRICK_SLAB = ITEMS.register("permafrost_brick_slab", () -> new ModBlockItem(ModBlocks.PERMAFROST_BRICK_SLAB.get(), props("permafrost_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CRACKED_PERMAFROST_BRICKS = ITEMS.register("cracked_permafrost_bricks", () -> new ModBlockItem(ModBlocks.CRACKED_PERMAFROST_BRICKS.get(), props("cracked_permafrost_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PERMAFROST_TILES = ITEMS.register("permafrost_tiles", () -> new ModBlockItem(ModBlocks.PERMAFROST_TILES.get(), props("permafrost_tiles").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_PERMAFROST_BRICKS = ITEMS.register("chiseled_permafrost_bricks", () -> new ModBlockItem(ModBlocks.CHISELED_PERMAFROST_BRICKS.get(), props("chiseled_permafrost_bricks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_PERMAFROST_BRICK_STAIRS = ITEMS.register("chiseled_permafrost_brick_stairs", () -> new ModBlockItem(ModBlocks.CHISELED_PERMAFROST_BRICK_STAIRS.get(), props("chiseled_permafrost_brick_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> CHISELED_PERMAFROST_BRICK_SLAB = ITEMS.register("chiseled_permafrost_brick_slab", () -> new ModBlockItem(ModBlocks.CHISELED_PERMAFROST_BRICK_SLAB.get(), props("chiseled_permafrost_brick_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_PERMAFROST = ITEMS.register("polished_permafrost", () -> new ModBlockItem(ModBlocks.POLISHED_PERMAFROST.get(), props("polished_permafrost").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_PERMAFROST_STAIRS = ITEMS.register("polished_permafrost_stairs", () -> new ModBlockItem(ModBlocks.POLISHED_PERMAFROST_STAIRS.get(), props("polished_permafrost_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> POLISHED_PERMAFROST_SLAB = ITEMS.register("polished_permafrost_slab", () -> new ModBlockItem(ModBlocks.POLISHED_PERMAFROST_SLAB.get(), props("polished_permafrost_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PERMAFROST_PILLAR = ITEMS.register("permafrost_pillar", () -> new ModBlockItem(ModBlocks.PERMAFROST_PILLAR.get(), props("permafrost_pillar").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> PERMAFROST_BRICK_WALL = ITEMS.register("permafrost_brick_wall", () -> new ModBlockItem(ModBlocks.PERMAFROST_BRICK_WALL.get(), props("permafrost_brick_wall").useBlockDescriptionPrefix()));

    public static final RegistryEntry<Item> GLACIAN_LOG = ITEMS.register("glacian_log", () -> new ModBlockItem(ModBlocks.GLACIAN_LOG.get(), props("glacian_log").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> STRIPPED_GLACIAN_LOG = ITEMS.register("stripped_glacian_log", () -> new ModBlockItem(ModBlocks.STRIPPED_GLACIAN_LOG.get(), props("stripped_glacian_log").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_LEAVES = ITEMS.register("glacian_leaves", () -> new ModBlockItem(ModBlocks.GLACIAN_LEAVES.get(), props("glacian_leaves").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_PLANKS = ITEMS.register("glacian_planks", () -> new ModBlockItem(ModBlocks.GLACIAN_PLANKS.get(), props("glacian_planks").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_STAIRS = ITEMS.register("glacian_stairs", () -> new ModBlockItem(ModBlocks.GLACIAN_STAIRS.get(), props("glacian_stairs").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_SLAB = ITEMS.register("glacian_slab", () -> new ModBlockItem(ModBlocks.GLACIAN_SLAB.get(), props("glacian_slab").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_DOOR = BASIC_ITEMS.register("glacian_door", () -> new ModBlockItem(ModBlocks.GLACIAN_DOOR.get(), props("glacian_door").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_TRAPDOOR = ITEMS.register("glacian_trapdoor", () -> new ModBlockItem(ModBlocks.GLACIAN_TRAPDOOR.get(), props("glacian_trapdoor").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_FENCE = ITEMS.register("glacian_fence", () -> new ModBlockItem(ModBlocks.GLACIAN_FENCE.get(), props("glacian_fence").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_FENCE_GATE = ITEMS.register("glacian_fence_gate", () -> new ModBlockItem(ModBlocks.GLACIAN_FENCE_GATE.get(), props("glacian_fence_gate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_BUTTON = ITEMS.register("glacian_button", () -> new ModBlockItem(ModBlocks.GLACIAN_BUTTON.get(), props("glacian_button").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_PRESSURE_PLATE = ITEMS.register("glacian_pressure_plate", () -> new ModBlockItem(ModBlocks.GLACIAN_PRESSURE_PLATE.get(), props("glacian_pressure_plate").useBlockDescriptionPrefix()));
    public static final RegistryEntry<Item> GLACIAN_FUR = ITEMS.register("glacian_fur", () -> new ModBlockItem(ModBlocks.GLACIAN_FUR.get(), props("glacian_fur").useBlockDescriptionPrefix()));

    // LEGACY ENTITIES. WILL BE REPLACED IN THE FUTURE
    public static final RegistryEntry<Item> LUNARIAN_SPAWN_EGG = SPAWN_EGGS.register("lunarian_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.LUNARIAN, 0xff33ccff, 0xff4e3923, props("lunarian_spawn_egg")));
    public static final RegistryEntry<Item> CORRUPTED_LUNARIAN_SPAWN_EGG = SPAWN_EGGS.register("corrupted_lunarian_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.CORRUPTED_LUNARIAN, 0xff1e1b19, 0xff0090c1, props("corrupted_lunarian_spawn_egg")));
    public static final RegistryEntry<Item> STAR_CRAWLER_SPAWN_EGG = SPAWN_EGGS.register("star_crawler_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.STAR_CRAWLER, 0xff333333, 0xff00cccc, props("star_crawler_spawn_egg")));
    public static final RegistryEntry<Item> MARTIAN_RAPTOR_SPAWN_EGG = SPAWN_EGGS.register("martian_raptor_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.MARTIAN_RAPTOR, 0x51a03e, 0xffffcc00, props("martian_raptor_spawn_egg")));
    public static final RegistryEntry<Item> PYGRO_SPAWN_EGG = SPAWN_EGGS.register("pygro_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.PYGRO, 0xffcc6600, 0xff990000, props("pygro_spawn_egg")));
    public static final RegistryEntry<Item> ZOMBIFIED_PYGRO_SPAWN_EGG = SPAWN_EGGS.register("zombified_pygro_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.ZOMBIFIED_PYGRO, 0x814a25, 0x5d8e47, props("zombified_pygro_spawn_egg")));
    public static final RegistryEntry<Item> PYGRO_BRUTE_SPAWN_EGG = SPAWN_EGGS.register("pygro_brute_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.PYGRO_BRUTE, 0xffcc6600, 0xfffef978, props("pygro_brute_spawn_egg")));
    public static final RegistryEntry<Item> MOGLER_SPAWN_EGG = SPAWN_EGGS.register("mogler_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.MOGLER, 0xffffcc00, 0xffcc0000, props("mogler_spawn_egg")));
    public static final RegistryEntry<Item> ZOMBIFIED_MOGLER_SPAWN_EGG = SPAWN_EGGS.register("zombified_mogler_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.ZOMBIFIED_MOGLER, 0xbf4e41, 0x79e655, props("zombified_mogler_spawn_egg")));
    public static final RegistryEntry<Item> SULFUR_CREEPER_SPAWN_EGG = SPAWN_EGGS.register("sulfur_creeper_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.SULFUR_CREEPER, 0xd48f30, 0xac791c, props("sulfur_creeper_spawn_egg")));
    public static final RegistryEntry<Item> GLACIAN_RAM_SPAWN_EGG = SPAWN_EGGS.register("glacian_ram_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.GLACIAN_RAM, 0xffe6ff, 0x433d3d, props("glacian_ram_spawn_egg")));
    public static final RegistryEntry<Item> LUNARIAN_WANDERING_TRADER_SPAWN_EGG = SPAWN_EGGS.register("lunarian_wandering_trader_spawn_egg", PlatformUtils.createSpawnEggItem(ModEntityTypes.LUNARIAN_WANDERING_TRADER, 0x5b73c7, 0x8244d5, props("lunarian_wandering_trader_spawn_egg")));

    public static Item.Properties props(String id) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, id)));
    }
}
