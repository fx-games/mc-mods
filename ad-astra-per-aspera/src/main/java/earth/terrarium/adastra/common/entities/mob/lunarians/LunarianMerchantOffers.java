package earth.terrarium.adastra.common.entities.mob.lunarians;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.utils.FluidUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// LEGACY ENTITY. WILL BE REPLACED IN THE FUTURE.
public class LunarianMerchantOffers {

    // 26.x made vanilla trades data driven (VillagerTrade/TradeSet); keep the legacy listing model locally.
    public interface ItemListing {
        @Nullable
        MerchantOffer getOffer(Entity entity, RandomSource random);
    }

    // Port of the removed 1.21 AbstractVillager#addOffersFromItemListings.
    public static void addOffers(Entity entity, RandomSource random, MerchantOffers offers, ItemListing[] listings, int max) {
        ArrayList<ItemListing> list = Lists.newArrayList(listings);
        int i = 0;
        while (i < max && !list.isEmpty()) {
            MerchantOffer offer = list.remove(random.nextInt(list.size())).getOffer(entity, random);
            if (offer != null) {
                offers.add(offer);
                i++;
            }
        }
    }

    public static final Map<ResourceKey<VillagerProfession>, Int2ObjectMap<ItemListing[]>> PROFESSION_TO_LEVELED_TRADE = Util.make(Maps.newHashMap(), map -> {
        map.put(VillagerProfession.FARMER, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.CHEESE.get(), 20, 20, 5), new BuyForOneEmeraldFactory(Items.GLOW_BERRIES, 22, 16, 3), new SellItemFactory(Items.BREAD, 1, 6, 16, 1)}, 2, new ItemListing[]{new SellItemFactory(Items.PUMPKIN_PIE, 1, 4, 5), new SellItemFactory(Items.GOLDEN_APPLE, 8, 1, 8, 20)}, 3, new ItemListing[]{new SellItemFactory(Items.COOKIE, 3, 18, 10), new BuyForOneEmeraldFactory(Blocks.MELON, 4, 12, 20)}, 4, new ItemListing[]{new SellItemFactory(Blocks.CAKE, 1, 1, 12, 15), new BuyForOneEmeraldFactory(Items.DIRT, 63, 16, 2), new SellSuspiciousStewFactory(MobEffects.NIGHT_VISION, 100, 15), new SellSuspiciousStewFactory(MobEffects.JUMP_BOOST, 160, 15), new SellSuspiciousStewFactory(MobEffects.WEAKNESS, 140, 15), new SellSuspiciousStewFactory(MobEffects.BLINDNESS, 120, 15), new SellSuspiciousStewFactory(MobEffects.POISON, 280, 15), new SellSuspiciousStewFactory(MobEffects.SATURATION, 7, 15)},
            5, new ItemListing[]{new SellItemFactory(Items.GOLDEN_CARROT, 3, 3, 30), new SellItemFactory(Items.GLISTERING_MELON_SLICE, 4, 3, 30)})));
        map.put(VillagerProfession.FISHERMAN, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.CHEESE.get(), 20, 20, 5), new BuyForOneEmeraldFactory(Items.STRING, 20, 16, 2), new BuyForOneEmeraldFactory(Items.COAL, 10, 16, 2), new ProcessItemFactory(Items.COD, 6, Items.COOKED_COD, 6, 16, 1),}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(Items.COD, 15, 16, 10), new ProcessItemFactory(Items.SALMON, 6, Items.COOKED_SALMON, 6, 16, 5), new SellItemFactory(Items.SOUL_CAMPFIRE, 2, 1, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.SALMON, 13, 16, 20), new SellEnchantedToolFactory(Items.FISHING_ROD, 3, 3, 10, 0.2f)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.TROPICAL_FISH, 6, 12, 30)}, 5, new ItemListing[]{new BuyForOneEmeraldFactory(Items.PUFFERFISH, 4, 12, 30), new SellItemFactory(new ItemStack(Items.WATER_BUCKET), 8, 1, 2, 9, 0.2f), new SellItemFactory(new ItemStack(Items.ICE), 4, 1, 8, 9, 0.2f)})));
        map.put(VillagerProfession.SHEPHERD,
            copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(Blocks.WOOL.white(), 18, 16, 2), new BuyForOneEmeraldFactory(Blocks.WOOL.brown(), 18, 16, 2), new BuyForOneEmeraldFactory(Blocks.WOOL.black(), 18, 16, 2), new BuyForOneEmeraldFactory(Blocks.WOOL.gray(), 18, 16, 2), new SellItemFactory(Items.SHEARS, 2, 1, 1)}, 2,
                new ItemListing[]{new BuyForOneEmeraldFactory(Items.DYE.white(), 12, 16, 10), new BuyForOneEmeraldFactory(Items.DYE.gray(), 12, 16, 10), new BuyForOneEmeraldFactory(Items.DYE.black(), 12, 16, 10), new BuyForOneEmeraldFactory(Items.DYE.lightBlue(), 12, 16, 10), new BuyForOneEmeraldFactory(Items.DYE.lime(), 12, 16, 10), new SellItemFactory(Blocks.WOOL.white(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.orange(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.magenta(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.lightBlue(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.yellow(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.lime(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.pink(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.gray(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.lightGray(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.cyan(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.purple(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.blue(), 1, 1, 16, 5),
                    new SellItemFactory(Blocks.WOOL.brown(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.green(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.red(), 1, 1, 16, 5), new SellItemFactory(Blocks.WOOL.black(), 1, 1, 16, 5), new SellItemFactory(Blocks.CARPET.white(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.orange(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.magenta(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.lightBlue(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.yellow(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.lime(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.pink(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.gray(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.lightGray(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.cyan(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.purple(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.blue(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.brown(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.green(), 1, 4, 16, 5),
                    new SellItemFactory(Blocks.CARPET.red(), 1, 4, 16, 5), new SellItemFactory(Blocks.CARPET.black(), 1, 4, 16, 5)},
                3,
                new ItemListing[]{new BuyForOneEmeraldFactory(Items.DYE.yellow(), 12, 16, 20), new BuyForOneEmeraldFactory(Items.DYE.lightGray(), 12, 16, 20), new BuyForOneEmeraldFactory(Items.DYE.orange(), 12, 16, 20), new BuyForOneEmeraldFactory(Items.DYE.red(), 12, 16, 20), new BuyForOneEmeraldFactory(Items.DYE.pink(), 12, 16, 20), new SellItemFactory(Blocks.BED.white(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.yellow(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.red(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.black(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.blue(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.brown(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.cyan(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.gray(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.green(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.lightBlue(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.lightGray(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.lime(), 3, 1, 12, 10),
                    new SellItemFactory(Blocks.BED.magenta(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.orange(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.pink(), 3, 1, 12, 10), new SellItemFactory(Blocks.BED.purple(), 3, 1, 12, 10)},
                4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.DYE.brown(), 12, 16, 30), new BuyForOneEmeraldFactory(Items.DYE.purple(), 12, 16, 30), new BuyForOneEmeraldFactory(Items.DYE.blue(), 12, 16, 30), new BuyForOneEmeraldFactory(Items.DYE.green(), 12, 16, 30), new BuyForOneEmeraldFactory(Items.DYE.magenta(), 12, 16, 30), new BuyForOneEmeraldFactory(Items.DYE.cyan(), 12, 16, 30), new SellItemFactory(ModItems.WHITE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.BLUE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.LIGHT_BLUE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.RED_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.PINK_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.GREEN_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.LIME_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.GRAY_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.PURPLE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.MAGENTA_FLAG.get(), 3, 1, 12, 25),
                    new SellItemFactory(ModItems.CYAN_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.BROWN_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.YELLOW_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.ORANGE_FLAG.get(), 3, 1, 12, 25)},
                5, new ItemListing[]{new SellItemFactory(ModItems.SPACE_PAINTING.get(), 64, 1, 2, 50)})));
        map.put(VillagerProfession.FLETCHER, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.IRON_ROD.get(), 8, 16, 6), new SellItemFactory(Items.ARROW, 1, 16, 1), new ProcessItemFactory(Blocks.GRAVEL, 10, Items.FLINT, 10, 12, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(Items.FLINT, 26, 12, 10), new SellItemFactory(Items.BOW, 2, 1, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.STRING, 14, 16, 20), new SellItemFactory(Items.CROSSBOW, 3, 1, 10)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.FEATHER, 24, 16, 30), new SellEnchantedToolFactory(Items.BOW, 2, 3, 15)}, 5, new ItemListing[]{new BuyForOneEmeraldFactory(Items.TRIPWIRE_HOOK, 8, 12, 30), new SellEnchantedToolFactory(Items.CROSSBOW, 3, 3, 15), new SellPotionHoldingItemFactory(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 30)})));
        map.put(VillagerProfession.LIBRARIAN, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(Items.PAPER, 24, 16, 2), new EnchantBookFactory(1, EnchantmentTags.TRADEABLE), new SellItemFactory(Blocks.BOOKSHELF, 9, 1, 12, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(Items.BOOK, 4, 12, 10), new EnchantBookFactory(5, EnchantmentTags.TRADEABLE), new SellItemFactory(Items.LANTERN, 1, 1, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.INK_SAC, 5, 12, 20), new EnchantBookFactory(10, EnchantmentTags.TRADEABLE), new SellItemFactory(Items.STAINED_GLASS.lightBlue(), 1, 8, 10)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.WRITABLE_BOOK, 2, 12, 30), new EnchantBookFactory(15, EnchantmentTags.TRADEABLE), new SellItemFactory(Items.CLOCK, 5, 1, 15), new SellItemFactory(Items.COMPASS, 4, 1, 15)}, 5, new ItemListing[]{new SellItemFactory(Items.NAME_TAG, 20, 1, 30)})));
        map.put(VillagerProfession.CARTOGRAPHER,
            copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(Items.PAPER, 24, 16, 2), new SellItemFactory(Items.MAP, 7, 1, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(Items.GLASS_PANE, 11, 16, 10)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.COMPASS, 1, 12, 20),}, 4,
                new ItemListing[]{new SellItemFactory(Items.ITEM_FRAME, 7, 1, 15), new SellItemFactory(ModItems.WHITE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.BLUE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.LIGHT_BLUE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.RED_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.PINK_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.GREEN_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.LIME_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.GRAY_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.PURPLE_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.MAGENTA_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.CYAN_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.BROWN_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.YELLOW_FLAG.get(), 3, 1, 12, 25), new SellItemFactory(ModItems.ORANGE_FLAG.get(), 3, 1, 12, 25)}, 5,
                new ItemListing[]{new SellItemFactory(Items.GLOBE_BANNER_PATTERN, 6, 1, 45)})));
        map.put(VillagerProfession.CLERIC, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(Items.ROTTEN_FLESH, 32, 16, 2), new SellItemFactory(Items.REDSTONE, 1, 2, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.DESH_INGOT.get(), 3, 8, 10), new SellItemFactory(Items.LAPIS_LAZULI, 1, 1, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.RABBIT_FOOT, 2, 12, 20), new SellItemFactory(Blocks.GLOWSTONE, 4, 1, 12, 10)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.TURTLE_SCUTE, 4, 12, 30), new BuyForOneEmeraldFactory(Items.GLASS_BOTTLE, 9, 12, 30), new SellItemFactory(Items.ENDER_PEARL, 5, 1, 15)}, 5, new ItemListing[]{new BuyForOneEmeraldFactory(Items.NETHER_WART, 22, 12, 30), new SellItemFactory(Items.EXPERIENCE_BOTTLE, 2, 1, 30), new SellItemFactory(ModItems.OXYGEN_BUCKET.get(), 32, 1, 1, 60)})));
        map.put(VillagerProfession.ARMORER, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.ICE_SHARD.get(), 12, 16, 2), new SellItemFactory(new ItemStack(ModItems.SPACE_PANTS.get()), 14, 1, 12, 4, 0.2f), new SellItemFactory(new ItemStack(ModItems.SPACE_BOOTS.get()), 8, 1, 12, 4, 0.2f), new SellItemFactory(new ItemStack(ModItems.SPACE_HELMET.get()), 10, 1, 12, 4, 0.2f), new SellItemFactory(new ItemStack(ModItems.SPACE_SUIT.get()), 36, 1, 12, 8, 0.2f)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.DESH_INGOT.get(), 4, 8, 10), new SellItemFactory(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f), new SellItemFactory(new ItemStack(ModItems.SPACE_BOOTS.get()), 8, 1, 12, 4, 0.2f), new SellItemFactory(new ItemStack(ModItems.SPACE_PANTS.get()), 14, 1, 12, 4, 0.2f)}, 3,
            new ItemListing[]{new BuyForOneEmeraldFactory(Items.LAVA_BUCKET, 1, 12, 20), new BuyForOneEmeraldFactory(Items.DIAMOND, 1, 12, 20), new SellItemFactory(new ItemStack(ModItems.SPACE_HELMET.get()), 10, 1, 12, 4, 0.2f), new SellItemFactory(new ItemStack(ModItems.SPACE_SUIT.get()), 36, 1, 12, 8, 0.2f), new SellItemFactory(new ItemStack(Items.SHIELD), 5, 1, 12, 10, 0.2f)}, 4, new ItemListing[]{new SellEnchantedToolFactory(ModItems.SPACE_PANTS.get(), 28, 3, 15, 0.2f), new SellEnchantedToolFactory(ModItems.SPACE_BOOTS.get(), 16, 3, 15, 0.2f)}, 5, new ItemListing[]{new SellEnchantedToolFactory(ModItems.SPACE_HELMET.get(), 16, 3, 30, 0.2f), new SellEnchantedToolFactory(ModItems.SPACE_SUIT.get(), 48, 3, 30, 0.2f), new SellItemFactory(FluidUtils.fluidFilledItem(ModItems.GAS_TANK, ModFluids.OXYGEN), 24, 1, 2, 40), new SellItemFactory(ModItems.OXYGEN_BUCKET.get(), 32, 1, 1, 60), new SellItemFactory(ModItems.OXYGEN_LOADER.get(), 48, 1, 1, 60), new SellItemFactory(ModItems.COAL_GENERATOR.get(), 32, 1, 1, 60)})));
        map.put(VillagerProfession.WEAPONSMITH,
            copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.ICE_SHARD.get(), 12, 16, 2), new SellItemFactory(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2f), new SellEnchantedToolFactory(Items.IRON_SWORD, 2, 3, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.DESH_INGOT.get(), 4, 12, 10), new SellItemFactory(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.FLINT, 24, 12, 20)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.DIAMOND, 1, 12, 30), new SellEnchantedToolFactory(Items.DIAMOND_AXE, 12, 3, 15, 0.2f)}, 5, new ItemListing[]{new SellEnchantedToolFactory(Items.DIAMOND_SWORD, 8, 3, 30, 0.2f), new SellItemFactory(FluidUtils.fluidFilledItem(ModItems.GAS_TANK, ModFluids.OXYGEN), 24, 1, 2, 40), new SellItemFactory(ModItems.OXYGEN_BUCKET.get(), 32, 1, 1, 60), new SellItemFactory(ModItems.OXYGEN_LOADER.get(), 48, 3, 1, 60)})));
        map.put(VillagerProfession.TOOLSMITH, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.ICE_SHARD.get(), 12, 16, 2), new SellItemFactory(new ItemStack(Items.SOUL_TORCH), 3, 16, 8, 1, 0.2f), new SellItemFactory(ModItems.WRENCH.get(), 14, 1, 2, 2), new SellItemFactory(new ItemStack(Items.STONE_AXE), 1, 1, 12, 1, 0.2f), new SellItemFactory(new ItemStack(Items.STONE_SHOVEL), 1, 1, 12, 1, 0.2f), new SellItemFactory(new ItemStack(Items.STONE_PICKAXE), 1, 1, 12, 1, 0.2f), new SellItemFactory(new ItemStack(Items.STONE_HOE), 1, 1, 12, 1, 0.2f)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.DESH_INGOT.get(), 4, 12, 10), new SellItemFactory(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2f)}, 3,
            new ItemListing[]{new BuyForOneEmeraldFactory(Items.FLINT, 30, 12, 20), new SellEnchantedToolFactory(Items.IRON_AXE, 1, 3, 10, 0.2f), new SellEnchantedToolFactory(Items.IRON_SHOVEL, 2, 3, 10, 0.2f), new SellEnchantedToolFactory(Items.IRON_PICKAXE, 3, 3, 10, 0.2f), new SellItemFactory(new ItemStack(Items.DIAMOND_HOE), 4, 1, 3, 10, 0.2f)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.DIAMOND, 1, 12, 30), new SellEnchantedToolFactory(Items.DIAMOND_AXE, 12, 3, 15, 0.2f), new SellEnchantedToolFactory(Items.DIAMOND_SHOVEL, 5, 3, 15, 0.2f)}, 5, new ItemListing[]{new SellEnchantedToolFactory(Items.DIAMOND_PICKAXE, 13, 3, 30, 0.2f), new SellItemFactory(FluidUtils.fluidFilledItem(ModItems.GAS_TANK, ModFluids.OXYGEN), 24, 1, 2, 40), new SellItemFactory(ModItems.OXYGEN_BUCKET.get(), 32, 1, 1, 60), new SellItemFactory(ModItems.OXYGEN_LOADER.get(), 48, 1, 1, 60), new SellItemFactory(ModItems.COAL_GENERATOR.get(), 32, 1, 1, 60)})));
        map.put(VillagerProfession.BUTCHER, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.CHEESE.get(), 20, 20, 5), new BuyForOneEmeraldFactory(Items.CHICKEN, 14, 16, 2), new BuyForOneEmeraldFactory(Items.PORKCHOP, 7, 16, 2), new BuyForOneEmeraldFactory(Items.RABBIT, 4, 16, 2), new SellItemFactory(Items.RABBIT_STEW, 1, 1, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(Items.COAL, 15, 16, 2), new SellItemFactory(Items.COOKED_PORKCHOP, 1, 5, 16, 5), new SellItemFactory(Items.COOKED_CHICKEN, 1, 8, 16, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.MUTTON, 7, 16, 20), new BuyForOneEmeraldFactory(Items.BEEF, 10, 16, 20)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.DRIED_KELP_BLOCK, 10, 12, 30)}, 5, new ItemListing[]{new BuyForOneEmeraldFactory(Items.SWEET_BERRIES, 10, 12, 30)})));
        map.put(VillagerProfession.LEATHERWORKER, copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(Items.LEATHER, 6, 16, 2), new SellDyedArmorFactory(Items.LEATHER_LEGGINGS, 3), new SellDyedArmorFactory(Items.LEATHER_CHESTPLATE, 7)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(Items.FLINT, 26, 12, 10), new SellDyedArmorFactory(Items.LEATHER_HELMET, 5, 12, 5), new SellDyedArmorFactory(Items.LEATHER_BOOTS, 4, 12, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(Items.RABBIT_HIDE, 9, 12, 20), new SellDyedArmorFactory(Items.LEATHER_CHESTPLATE, 7)}, 4, new ItemListing[]{new BuyForOneEmeraldFactory(Items.TURTLE_SCUTE, 4, 12, 30), new SellDyedArmorFactory(Items.LEATHER_HORSE_ARMOR, 6, 12, 15)}, 5, new ItemListing[]{new SellItemFactory(new ItemStack(Items.SADDLE), 6, 1, 12, 30, 0.2f), new SellDyedArmorFactory(Items.LEATHER_HELMET, 5, 12, 30)})));
        map.put(VillagerProfession.MASON,
            copyToFastUtilMap(Map.of(1, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.CONGLOMERATE.get(), 10, 16, 2), new BuyForOneEmeraldFactory(ModItems.MOON_SAND.get(), 32, 32, 1), new SellItemFactory(ModItems.MOON_STONE_BRICKS.get(), 1, 10, 16, 1)}, 2, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.MOON_STONE.get(), 20, 16, 10), new SellItemFactory(ModItems.CHISELED_MOON_STONE_BRICKS.get(), 1, 4, 16, 5)}, 3, new ItemListing[]{new BuyForOneEmeraldFactory(ModItems.MARS_STONE.get(), 16, 16, 20), new BuyForOneEmeraldFactory(ModItems.VENUS_STONE.get(), 16, 16, 20), new BuyForOneEmeraldFactory(ModItems.MERCURY_STONE.get(), 16, 16, 20), new SellItemFactory(Blocks.DRIPSTONE_BLOCK, 1, 4, 16, 10), new SellItemFactory(ModItems.POLISHED_MARS_STONE.get(), 1, 4, 16, 10), new SellItemFactory(ModItems.POLISHED_VENUS_STONE.get(), 1, 4, 16, 10), new SellItemFactory(ModItems.POLISHED_MERCURY_STONE.get(), 1, 4, 16, 10)}, 4,
                new ItemListing[]{new BuyForOneEmeraldFactory(Items.QUARTZ, 12, 12, 30), new SellItemFactory(Blocks.DYED_TERRACOTTA.orange(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.white(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.blue(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.lightBlue(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.gray(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.lightGray(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.black(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.red(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.pink(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.magenta(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.lime(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.green(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.cyan(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.purple(), 1, 1, 12, 15), new SellItemFactory(Blocks.DYED_TERRACOTTA.yellow(), 1, 1, 12, 15),
                    new SellItemFactory(Blocks.DYED_TERRACOTTA.brown(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.orange(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.white(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.blue(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.lightBlue(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.gray(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.lightGray(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.black(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.red(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.pink(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.magenta(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.lime(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.green(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.cyan(), 1, 1, 12, 15),
                    new SellItemFactory(Blocks.GLAZED_TERRACOTTA.purple(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.yellow(), 1, 1, 12, 15), new SellItemFactory(Blocks.GLAZED_TERRACOTTA.brown(), 1, 1, 12, 15)},
                5, new ItemListing[]{new SellItemFactory(ModItems.STEEL_PLATING.get(), 1, 8, 12, 15), new SellItemFactory(ModItems.DESH_PLATING.get(), 1, 8, 12, 15), new SellItemFactory(ModItems.OSTRUM_PLATING.get(), 1, 4, 12, 15), new SellItemFactory(ModItems.STEEL_DOOR.get(), 1, 3, 12, 15), new SellItemFactory(Blocks.QUARTZ_PILLAR, 1, 1, 12, 30), new SellItemFactory(Blocks.QUARTZ_BLOCK, 1, 1, 12, 30)})));
    });

    public static final Int2ObjectMap<ItemListing[]> WANDERING_TRADER_TRADES = copyToFastUtilMap(Map.of(1, new ItemListing[]{new SellItemFactory(ModItems.CHEESE.get(), 1, 3, 6, 1), new SellItemFactory(ModItems.SPACE_PAINTING.get(), 48, 1, 2, 2), new SellItemFactory(ModItems.GAS_TANK.get(), 10, 1, 3, 1), new SellItemFactory(Items.SOUL_TORCH, 1, 12, 12, 1), new SellItemFactory(Items.SOUL_CAMPFIRE, 3, 1, 4, 1), new SellItemFactory(ModItems.WRENCH.get(), 12, 1, 2, 2), new SellItemFactory(ModItems.WHITE_FLAG.get(), 10, 1, 3, 2), new SellItemFactory(ModItems.SPACE_HELMET.get(), 10, 1, 2, 1), new SellItemFactory(ModItems.SPACE_SUIT.get(), 16, 1, 2, 1), new SellItemFactory(ModItems.SPACE_PANTS.get(), 14, 1, 2, 1), new SellItemFactory(ModItems.SPACE_BOOTS.get(), 8, 1, 2, 1), new SellItemFactory(Items.COAL, 1, 5, 4, 1), new SellItemFactory(ModItems.OXYGEN_BUCKET.get(), 10, 1, 2, 2), new SellItemFactory(Items.COPPER_INGOT, 1, 4, 12, 1),
        new SellItemFactory(Items.WATER_BUCKET, 5, 1, 4, 1), new SellItemFactory(Items.LAVA_BUCKET, 3, 1, 4, 1), new BuyForOneEmeraldFactory(ModItems.DESH_INGOT.get(), 4, 20, 1), new BuyForOneEmeraldFactory(ModItems.OSTRUM_INGOT.get(), 4, 20, 1), new BuyForOneEmeraldFactory(ModItems.CALORITE_INGOT.get(), 4, 20, 1)}, 2, new ItemListing[]{new SellItemFactory(FluidUtils.fluidFilledItem(ModItems.GAS_TANK, ModFluids.OXYGEN), 16, 1, 3, 2), new SellItemFactory(ModItems.LAUNCH_PAD.get(), 3, 1, 3, 1), new SellItemFactory(ModItems.LAUNCH_PAD.get(), 3, 1, 3, 1), new SellItemFactory(ModItems.GLACIAN_LOG.get(), 5, 16, 20, 1), new SellItemFactory(ModItems.GLACIAN_LEAVES.get(), 5, 16, 20, 1), new SellItemFactory(ModItems.AERONOS_STEM.get(), 5, 16, 20, 1), new SellItemFactory(ModItems.STROPHAR_STEM.get(), 5, 16, 20, 1), new SellItemFactory(ModItems.FUEL_BUCKET.get(), 4, 1, 6, 1)}));

    private static Int2ObjectMap<ItemListing[]> copyToFastUtilMap(Map<Integer, ItemListing[]> map) {
        return new Int2ObjectOpenHashMap<>(map);
    }

    static class BuyForOneEmeraldFactory implements ItemListing {

        private final Item buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public BuyForOneEmeraldFactory(ItemLike item, int price, int maxUses, int experience) {
            this.buy = item.asItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05f;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemCost itemStack = new ItemCost(this.buy, this.price);
            return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellItemFactory implements ItemListing {

        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        public SellItemFactory(Item item, int price, int count, int experience) {
            this(new ItemStack(item), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
            this(stack, price, count, maxUses, experience, 0.05f);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack stack = new ItemStack(this.sell.getItem(), this.count);
            stack.applyComponents(this.sell.getComponentsPatch());
            return new MerchantOffer(new ItemCost(Items.EMERALD, this.price), stack, this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellSuspiciousStewFactory implements ItemListing {

        private final SuspiciousStewEffects effects;
        final int duration;
        final int experience;
        private final float multiplier;

        public SellSuspiciousStewFactory(Holder<MobEffect> effect, int duration, int experience) {
            this.effects = SuspiciousStewEffects.EMPTY.withEffectAdded(new SuspiciousStewEffects.Entry(effect, duration));
            this.duration = duration;
            this.experience = experience;
            this.multiplier = 0.05f;
        }

        @Override
        @Nullable
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            itemStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.effects);
            return new MerchantOffer(new ItemCost(Items.EMERALD, 1), itemStack, 12, this.experience, this.multiplier);
        }
    }

    static class ProcessItemFactory implements ItemListing {

        private final ItemStack secondBuy;
        private final int secondCount;
        private final int price;
        private final ItemStack sell;
        private final int sellCount;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public ProcessItemFactory(ItemLike item, int secondCount, Item sellItem, int sellCount, int maxUses, int experience) {
            this(item, secondCount, 1, sellItem, sellCount, maxUses, experience);
        }

        public ProcessItemFactory(ItemLike item, int secondCount, int price, Item sellItem, int sellCount, int maxUses, int experience) {
            this.secondBuy = new ItemStack(item);
            this.secondCount = secondCount;
            this.price = price;
            this.sell = new ItemStack(sellItem);
            this.sellCount = sellCount;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05f;
        }

        @Override
        @Nullable
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(new ItemCost(Items.EMERALD, this.price), Optional.of(new ItemCost(this.secondBuy.getItem(), this.secondCount)), new ItemStack(this.sell.getItem(), this.sellCount), this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellEnchantedToolFactory implements ItemListing {

        private final ItemStack tool;
        private final int basePrice;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellEnchantedToolFactory(Item item, int basePrice, int maxUses, int experience) {
            this(item, basePrice, maxUses, experience, 0.05f);
        }

        public SellEnchantedToolFactory(Item item, int basePrice, int maxUses, int experience, float multiplier) {
            this.tool = new ItemStack(item);
            this.basePrice = basePrice;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            int i = 5 + random.nextInt(15);
            RegistryAccess registryaccess = entity.level().registryAccess();
            Optional<HolderSet.Named<Enchantment>> optional = registryaccess.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.ON_TRADED_EQUIPMENT);
            ItemStack itemStack = EnchantmentHelper.enchantItem(random, new ItemStack(this.tool.getItem()), i, registryaccess, optional);

            int j = Math.min(this.basePrice + i, 64);
            ItemCost itemStack2 = new ItemCost(Items.EMERALD, j);
            return new MerchantOffer(itemStack2, itemStack, this.maxUses, this.experience, this.multiplier);
        }
    }

    static class SellPotionHoldingItemFactory implements ItemListing {

        private final ItemStack sell;
        private final int sellCount;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final Item secondBuy;
        private final int secondCount;
        private final float priceMultiplier;

        public SellPotionHoldingItemFactory(Item arrow, int secondCount, Item tippedArrow, int sellCount, int price, int maxUses, int experience) {
            this.sell = new ItemStack(tippedArrow);
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.secondBuy = arrow;
            this.secondCount = secondCount;
            this.sellCount = sellCount;
            this.priceMultiplier = 0.05f;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemCost itemStack = new ItemCost(Items.EMERALD, this.price);
            List list = BuiltInRegistries.POTION.listElements().filter((value) -> !value.value().getEffects().isEmpty() && entity.level().potionBrewing().isBrewablePotion(value)).collect(Collectors.toList());
            Holder<Potion> holder = (Holder)Util.getRandom(list, random);
            ItemStack itemStack2 = new ItemStack(this.sell.getItem(), this.sellCount);
            itemStack2.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
            return new MerchantOffer(itemStack, Optional.of(new ItemCost(this.secondBuy, this.secondCount)), itemStack2, this.maxUses, this.experience, this.priceMultiplier);
        }
    }

    static class EnchantBookFactory implements ItemListing {

        private final int experience;
        private final TagKey<Enchantment> tradeableEnchantments;

        public EnchantBookFactory(int experience, TagKey<Enchantment> tradeableEnchantments) {
            this.experience = experience;
            this.tradeableEnchantments = tradeableEnchantments;
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            Optional<Holder<Enchantment>> optional = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getRandomElementOf(this.tradeableEnchantments, random);
            int i;
            ItemStack itemStack;
            if (!optional.isEmpty()) {
                Holder<Enchantment> holder = optional.get();
                Enchantment enchantment = (Enchantment)holder.value();
                int j = Math.max(enchantment.getMinLevel(), 0);
                int k = Math.min(enchantment.getMaxLevel(), Integer.MAX_VALUE);
                int l = Mth.nextInt(random, j, k);
                itemStack = EnchantmentHelper.createBook(new EnchantmentInstance(holder, l));
                i = 2 + random.nextInt(5 + l * 10) + 3 * l;
                if (holder.is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                    i *= 2;
                }

                if (i > 64) {
                    i = 64;
                }
            } else {
                i = 1;
                itemStack = new ItemStack(Items.BOOK);
            }
            return new MerchantOffer(new ItemCost(Items.EMERALD, i), Optional.of(new ItemCost(Items.BOOK)), itemStack, 12, this.experience, 0.2f);
        }
    }

    static class SellDyedArmorFactory implements ItemListing {

        private final Item sell;
        private final int price;
        private final int maxUses;
        private final int experience;

        public SellDyedArmorFactory(Item item, int price) {
            this(item, price, 12, 1);
        }

        public SellDyedArmorFactory(Item item, int price, int maxUses, int experience) {
            this.sell = item;
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        private static DyeColor getDye(RandomSource random) {
            return DyeColor.byId(random.nextInt(16));
        }

        @Override
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemCost itemStack = new ItemCost(Items.EMERALD, this.price);
            ItemStack itemStack2 = new ItemStack(this.sell);
            if (true) { // 26.x: ItemTags.DYEABLE removed; all listed items are leather
                ArrayList<DyeColor> list = Lists.newArrayList();
                list.add(SellDyedArmorFactory.getDye(random));
                if (random.nextFloat() > 0.7f) {
                    list.add(SellDyedArmorFactory.getDye(random));
                }
                if (random.nextFloat() > 0.8f) {
                    list.add(SellDyedArmorFactory.getDye(random));
                }
                itemStack2 = DyedItemColor.applyDyes(itemStack2, list);
            }
            return new MerchantOffer(itemStack, itemStack2, this.maxUses, this.experience, 0.2f);
        }
    }
}
