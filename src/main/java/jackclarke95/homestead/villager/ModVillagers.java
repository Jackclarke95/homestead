package jackclarke95.homestead.villager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.FilledMapItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.map.MapState;
import net.minecraft.item.map.MapDecorationTypes;

public class ModVillagers {

    // Move helper classes to the top for visibility
    static class EnchantmentOption {
        String enchantmentId;
        int level;
        int minCost;
        int maxCost;

        EnchantmentOption(String enchantmentId, int level, int minCost, int maxCost) {
            this.enchantmentId = enchantmentId;
            this.level = level;
            this.minCost = minCost;
            this.maxCost = maxCost;
        }
    }

    static class ItemTrade {
        ItemStack item;
        int minPrice, maxPrice;

        ItemTrade(ItemStack item, int minPrice, int maxPrice) {
            this.item = item;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }
    }

    public static final RegistryKey<PointOfInterestType> SOWING_BED_POI_KEY = registerPoiKey("botanist_poi");
    public static final PointOfInterestType SOWING_BED_POI = registerPOI("botanist_poi", ModBlocks.SOWING_BED);

    public static final VillagerProfession botanist = registerProfession("botanist", SOWING_BED_POI_KEY);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(Homestead.MOD_ID, name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_FARMER));
    }

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(Identifier.of(Homestead.MOD_ID, name),
                1, 1, block);
    }

    private static RegistryKey<PointOfInterestType> registerPoiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(Homestead.MOD_ID, name));
    }

    public static void registerVillagers() {
        Homestead.LOGGER.debug("Registering Mod Villagers for " + Homestead.MOD_ID);
    }

    public static void registerVillagerTrades() {
        final float LOW_MULTIPLIER = 0.02f;
        final float HIGH_MULTIPLIER = 0.05f;

        // Level 1 - Slot 1: Fruit trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 1, factories -> {
            factories.add((entity, random) -> {

                ItemTrade[] trades = new ItemTrade[] {
                        new ItemTrade(new ItemStack(ModItems.PEAR), 24, 28),
                        new ItemTrade(new ItemStack(Items.APPLE), 24, 28),
                        new ItemTrade(new ItemStack(ModItems.LEMON), 24, 28),
                        new ItemTrade(new ItemStack(ModItems.ORANGE), 24, 28),
                        new ItemTrade(new ItemStack(ModItems.APRICOT), 24, 28),
                        new ItemTrade(new ItemStack(ModItems.PEACH), 24, 28),
                        new ItemTrade(new ItemStack(ModItems.PLUM), 24, 28),
                        new ItemTrade(new ItemStack(ModItems.RASPBERRY), 16, 20),
                        new ItemTrade(new ItemStack(ModItems.BLACKBERRY), 16, 20)
                };

                ItemTrade selected = trades[random.nextInt(trades.length)];
                int price = getRandomPrice(random, selected.minPrice, selected.maxPrice);

                return new TradeOffer(
                        new TradedItem(selected.item.getItem(), price),
                        new ItemStack(Items.EMERALD, 1), 16, 2, LOW_MULTIPLIER);
            });
        });

        // Level 1 - Slot 2: Tool trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 1, factories -> {
            factories.add((entity, random) -> {
                ItemStack[] toolOptions = new ItemStack[] {
                        new ItemStack(ModItems.IRON_CROOK, 1),
                        new ItemStack(Items.SHEARS, 1)
                };

                ItemStack selectedTool = toolOptions[random.nextInt(toolOptions.length)];

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, 1),
                        selectedTool, 3, 2, HIGH_MULTIPLIER);
            });
        });

        // Level 2 - Slot 1: Sapling trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 2, factories -> {
            factories.add((entity, random) -> {
                ItemTrade[] saplingTrades = new ItemTrade[] {
                        new ItemTrade(new ItemStack(Items.OAK_SAPLING), 1, 1),
                        new ItemTrade(new ItemStack(Items.SPRUCE_SAPLING), 1, 1),
                        new ItemTrade(new ItemStack(Items.BIRCH_SAPLING), 1, 1),
                        new ItemTrade(new ItemStack(Items.JUNGLE_SAPLING), 1, 1),
                        new ItemTrade(new ItemStack(Items.ACACIA_SAPLING), 1, 1),
                        new ItemTrade(new ItemStack(Items.DARK_OAK_SAPLING), 2, 4),
                        new ItemTrade(new ItemStack(Items.CHERRY_SAPLING), 1, 2),
                        new ItemTrade(new ItemStack(Items.MANGROVE_PROPAGULE), 2, 4),
                        new ItemTrade(new ItemStack(ModBlocks.APPLE_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(ModBlocks.PEAR_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(ModBlocks.PLUM_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(ModBlocks.LEMON_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(ModBlocks.ORANGE_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(ModBlocks.APRICOT_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(ModBlocks.PEACH_TREE_SAPLING), 4, 7),
                        new ItemTrade(new ItemStack(Blocks.AZALEA), 5, 9),
                        new ItemTrade(new ItemStack(Blocks.FLOWERING_AZALEA), 5, 9),
                };

                ItemTrade selected = saplingTrades[random.nextInt(saplingTrades.length)];
                int price = getRandomPrice(random, selected.minPrice, selected.maxPrice);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price),
                        selected.item, 5, 5, LOW_MULTIPLIER);
            });
        });

        // Level 2 - Slot 2: Leaf blocks trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 2, factories -> {
            factories.add((entity, random) -> {
                ItemStack[] leafOptions = {
                        new ItemStack(Items.OAK_LEAVES),
                        new ItemStack(Items.SPRUCE_LEAVES),
                        new ItemStack(Items.BIRCH_LEAVES),
                        new ItemStack(Items.JUNGLE_LEAVES),
                        new ItemStack(Items.ACACIA_LEAVES),
                        new ItemStack(Items.DARK_OAK_LEAVES),
                        new ItemStack(Items.CHERRY_LEAVES),
                        new ItemStack(Items.MANGROVE_LEAVES),
                        new ItemStack(Items.AZALEA_LEAVES),
                        new ItemStack(Items.FLOWERING_AZALEA_LEAVES)
                };

                ItemStack selectedLeaves = leafOptions[random.nextInt(leafOptions.length)];
                selectedLeaves.setCount(8);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, 1),
                        selectedLeaves, 5, 5, LOW_MULTIPLIER);
            });
        });

        // Level 3 - Enchanted Iron Crook trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 3, factories -> {
            factories.add((entity, random) -> {
                ItemStack enchantedCrook = new ItemStack(ModItems.IRON_CROOK);

                EnchantmentOption[] options = {
                        new EnchantmentOption("unbreaking", 1, 6, 18),
                        new EnchantmentOption("unbreaking", 2, 6, 18),
                        new EnchantmentOption("unbreaking", 3, 6, 18),
                        new EnchantmentOption("efficiency", 1, 6, 18),
                        new EnchantmentOption("efficiency", 2, 6, 18),
                        new EnchantmentOption("efficiency", 3, 6, 18),
                        new EnchantmentOption("efficiency", 4, 6, 18),
                        new EnchantmentOption("efficiency", 5, 6, 18),
                        new EnchantmentOption("looting", 1, 6, 18),
                        new EnchantmentOption("looting", 2, 6, 18),
                        new EnchantmentOption("looting", 3, 6, 18),
                        new EnchantmentOption("mending", 1, 6, 18),
                        new EnchantmentOption("silk_touch", 1, 18, 45),
                        new EnchantmentOption("mending", 1, 18, 45)
                };

                EnchantmentOption selected = options[random.nextInt(options.length)];
                applyEnchantment(enchantedCrook, selected.enchantmentId, selected.level, (VillagerEntity) entity);

                int cost = getRandomPrice(random, selected.minCost, selected.maxCost);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, cost),
                        enchantedCrook, 3, 10, HIGH_MULTIPLIER);
            });
        });

        // Level 3 - Flower Seeds trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 3, factories -> {
            factories.add((entity, random) -> {
                ItemTrade[] seedTrades = new ItemTrade[] {
                        new ItemTrade(new ItemStack(ModItems.AZURE_BLUET_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.BLUE_ORCHID_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.CORNFLOWER_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.DANDELION_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.LILY_OF_THE_VALLEY_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.ORANGE_TULIP_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.OXEYE_DAISY_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.PINK_TULIP_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.POPPY_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.RED_TULIP_SEEDS), 3, 5),
                        new ItemTrade(new ItemStack(ModItems.WHITE_TULIP_SEEDS), 3, 5)
                };

                ItemTrade selected = seedTrades[random.nextInt(seedTrades.length)];
                int price = getRandomPrice(random, selected.minPrice, selected.maxPrice);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price),
                        selected.item, 5, 10, LOW_MULTIPLIER);
            });
        });

        // Level 4 - Enchanted Diamond Crook trades
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 4, factories -> {
            factories.add((entity, random) -> {
                ItemStack enchantedCrook = new ItemStack(ModItems.DIAMOND_CROOK);

                EnchantmentOption[] options = {
                        new EnchantmentOption("unbreaking", 1, 12, 24),
                        new EnchantmentOption("unbreaking", 2, 12, 24),
                        new EnchantmentOption("unbreaking", 3, 12, 24),
                        new EnchantmentOption("efficiency", 1, 12, 24),
                        new EnchantmentOption("efficiency", 2, 12, 24),
                        new EnchantmentOption("efficiency", 3, 12, 24),
                        new EnchantmentOption("efficiency", 4, 12, 24),
                        new EnchantmentOption("efficiency", 5, 12, 24),
                        new EnchantmentOption("looting", 1, 12, 24),
                        new EnchantmentOption("looting", 2, 12, 24),
                        new EnchantmentOption("looting", 3, 12, 24),
                        new EnchantmentOption("mending", 1, 12, 24),
                        new EnchantmentOption("silk_touch", 1, 18, 30),
                        new EnchantmentOption("mending", 1, 18, 30),
                };

                EnchantmentOption selected = options[random.nextInt(options.length)];
                applyEnchantment(enchantedCrook, selected.enchantmentId, selected.level, (VillagerEntity) entity);

                int cost = getRandomPrice(random, selected.minCost, selected.maxCost);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, cost),
                        enchantedCrook, 3, 15, HIGH_MULTIPLIER);
            });
        });

        // Level 4 - Biome exploration map trade
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 1, factories -> {
            factories.add((entity, random) -> {
                // Biomes for exploration maps
                List<RegistryKey<Biome>> targetBiomes = List.of(
                        BiomeKeys.CHERRY_GROVE,
                        BiomeKeys.MUSHROOM_FIELDS,
                        BiomeKeys.FLOWER_FOREST,
                        BiomeKeys.MEADOW,
                        BiomeKeys.GROVE,
                        BiomeKeys.MANGROVE_SWAMP,
                        BiomeKeys.SUNFLOWER_PLAINS,
                        BiomeKeys.DARK_FOREST,
                        BiomeKeys.TAIGA);

                RegistryKey<Biome> selectedBiome = targetBiomes.get(random.nextInt(targetBiomes.size()));

                // Find the closest biome location relative to the villager
                ServerWorld world = (ServerWorld) entity.getWorld();
                BlockPos villagerPos = entity.getBlockPos();

                // Find the closest instance of the selected biome
                var biomeResult = world.locateBiome(
                        biomeEntry -> biomeEntry.matchesKey(selectedBiome),
                        villagerPos,
                        8192,
                        128,
                        128);

                ItemStack map;
                if (biomeResult != null) {
                    BlockPos biomePos = biomeResult.getFirst();

                    map = createExplorerMapWithRedX(world, biomePos);
                } else {
                    map = FilledMapItem.createMap(world, villagerPos.getX(), villagerPos.getZ(), (byte) 0, true, true);
                }

                String biomeKey = selectedBiome.getValue().getPath();
                map.set(DataComponentTypes.CUSTOM_NAME,
                        Text.translatable("item.homestead.explorer_map." + biomeKey));

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, 13),
                        Optional.of(new TradedItem(Items.COMPASS, 1)),
                        map, 5, 15, LOW_MULTIPLIER);
            });
        });

        // Level 5 - Special loot/tools trade
        TradeOfferHelper.registerVillagerOffers(ModVillagers.botanist, 5, factories -> {
            factories.add((entity, random) -> {
                ItemTrade[] lootTrades = new ItemTrade[] {
                        new ItemTrade(new ItemStack(Items.TORCHFLOWER_SEEDS), 12, 41),
                        new ItemTrade(new ItemStack(Items.MOSS_BLOCK), 4, 7),
                        new ItemTrade(new ItemStack(Items.MOSS_CARPET, 4), 2, 4),
                        new ItemTrade(new ItemStack(Items.VINE, 8), 2, 4),
                };

                ItemTrade selected = lootTrades[random.nextInt(lootTrades.length)];
                int price = getRandomPrice(random, selected.minPrice, selected.maxPrice);

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, price),
                        selected.item, 5, 30, LOW_MULTIPLIER);
            });
        });

    }

    private static int getRandomPrice(net.minecraft.util.math.random.Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static void applyEnchantment(ItemStack item, String enchantmentId, int level, VillagerEntity entity) {
        var registryManager = entity.getWorld().getRegistryManager();
        var enchantmentRegistry = registryManager.get(RegistryKeys.ENCHANTMENT);

        enchantmentRegistry.streamEntries().forEach(entry -> {
            var enchantment = entry.value();
            var id = enchantmentRegistry.getId(enchantment);
            if (id != null && id.getPath().equals(enchantmentId)) {
                item.addEnchantment(entry, level);
            }
        });
    }

    // Helper method to create a map with a persistent red X marker at the target
    // location
    // Helper method to create a map with a persistent red X marker (1.21.1+)
    public static ItemStack createExplorerMapWithRedX(ServerWorld world, BlockPos target) {
        ItemStack mapStack = FilledMapItem.createMap(world, target.getX(), target.getZ(), (byte) 0, true, true);
        FilledMapItem.fillExplorationMap(world, mapStack);

        // Tag the map with a custom data component to identify it as a special red X
        // map
        net.minecraft.nbt.NbtCompound tag = new net.minecraft.nbt.NbtCompound();
        tag.putString("homestead_red_x", target.getX() + "," + target.getZ());
        mapStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));

        // Add the red X marker at target location
        ensureRedXDecoration(mapStack, world, target);
        return mapStack;
    }

    // Utility to check for the tag and re-add the red X marker
    public static void ensureRedXDecoration(ItemStack mapStack, ServerWorld world, BlockPos target) {
        MapIdComponent mapId = mapStack.get(DataComponentTypes.MAP_ID);
        if (mapId != null) {
            MapState mapState = world.getMapState(mapId);
            if (mapState != null) {
                try {
                    var redXDecoration = MapDecorationTypes.RED_X;
                    Method addDecorationMethod = MapState.class.getDeclaredMethod("addDecoration",
                            net.minecraft.registry.entry.RegistryEntry.class,
                            net.minecraft.world.WorldAccess.class,
                            String.class,
                            double.class,
                            double.class,
                            double.class,
                            net.minecraft.text.Text.class);
                    addDecorationMethod.setAccessible(true);

                    addDecorationMethod.invoke(mapState,
                            redXDecoration,
                            world,
                            "target",
                            (double) target.getX(),
                            (double) target.getZ(),
                            180.0,
                            null);
                } catch (Exception e) {
                    System.out.println("Failed to add red X decoration: " + e.getMessage());
                }
            }
        }
    }
}
