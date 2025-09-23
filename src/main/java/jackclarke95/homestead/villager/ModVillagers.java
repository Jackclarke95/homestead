package jackclarke95.homestead.villager;

import java.util.List;

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
import net.minecraft.item.map.MapState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecorationTypes;
import java.lang.reflect.Method;
import net.minecraft.block.MapColor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;

public class ModVillagers {
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
        Homestead.LOGGER.info("Registering Mod Villagers for " + Homestead.MOD_ID);
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
                        // BiomeKeys.PLAINS,
                        // BiomeKeys.FOREST,
                        // BiomeKeys.BIRCH_FOREST,
                        BiomeKeys.DARK_FOREST
                // BiomeKeys.OLD_GROWTH_BIRCH_FOREST
                );

                RegistryKey<Biome> selectedBiome = targetBiomes.get(random.nextInt(targetBiomes.size()));

                // Find the closest biome location relative to the villager
                ServerWorld world = (ServerWorld) entity.getWorld();
                BlockPos villagerPos = entity.getBlockPos();

                // Find the closest instance of the selected biome
                int searchRadius = 2048; // much faster than 6400
                var biomeResult = world.locateBiome(
                        biomeEntry -> biomeEntry.matchesKey(selectedBiome),
                        villagerPos,
                        searchRadius,
                        64,
                        1);

                ItemStack map;
                if (biomeResult != null) {
                    BlockPos biomePos = biomeResult.getFirst();

                    // Create exploration map with X marker like vanilla cartographer
                    map = createExplorerMap(world, villagerPos, biomePos, (byte) 2, true);

                } else {
                    map = FilledMapItem.createMap(world, villagerPos.getX(), villagerPos.getZ(), (byte) 2, true, true);
                }
                map.set(DataComponentTypes.CUSTOM_NAME,
                        Text.translatable("item.homestead.biome_map",
                                selectedBiome.getValue().getPath()));

                int mapPrice = getRandomPrice(random, 12, 20);
                return new TradeOffer(
                        new TradedItem(Items.EMERALD, mapPrice),
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

    private static class EnchantmentOption {
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

    private static class ItemTrade {
        ItemStack item;
        int minPrice, maxPrice;

        ItemTrade(ItemStack item, int minPrice, int maxPrice) {
            this.item = item;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }
    }

    // Helper method to create explorer map with red X marker like vanilla
    // cartographer
    private static ItemStack createExplorerMap(ServerWorld world, BlockPos origin, BlockPos target, byte scale,
            boolean showIcons) {
        // Create the map centered on the target location
        ItemStack map = FilledMapItem.createMap(world, target.getX(), target.getZ(), scale, true, false);

        // Apply our fixed exploration map parchment effect
        fillExplorationMapFixed(world, map);

        // Add the red X marker at target location
        MapIdComponent mapId = map.get(DataComponentTypes.MAP_ID);
        if (mapId != null) {
            MapState mapState = world.getMapState(mapId);
            if (mapState != null) {
                try {
                    // Add the red X marker using reflection
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
                            net.minecraft.text.Text.empty()); // No label

                } catch (Exception e) {
                    System.out.println("Failed to add red X decoration: " + e.getMessage());
                }
            }
        }

        return map;
    }

    // Fixed version of FilledMapItem.fillExplorationMap with coordinate bugs
    // corrected and actual terrain sampling instead of biome tags
    private static void fillExplorationMapFixed(ServerWorld world, ItemStack map) {
        MapState mapState = FilledMapItem.getMapState(map, world);
        if (mapState != null) {
            if (world.getRegistryKey() == mapState.dimension) {
                int blocksPerPixel = 1 << mapState.scale;
                int centerX = mapState.centerX;
                int centerZ = mapState.centerZ;
                boolean[] waterBlocks = new boolean[16384]; // 128 * 128
                int mapOffsetX = centerX / blocksPerPixel - 64;
                int mapOffsetZ = centerZ / blocksPerPixel - 64;
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                // First pass: sample actual terrain and check for water blocks
                for (int mapX = 0; mapX < 128; ++mapX) {
                    for (int mapZ = 0; mapZ < 128; ++mapZ) {
                        int worldX = (mapOffsetX + mapX) * blocksPerPixel;
                        int worldZ = (mapOffsetZ + mapZ) * blocksPerPixel;

                        // Get surface height and check multiple levels for water
                        int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, worldX, worldZ);

                        boolean foundWater = false;

                        // Check surface level and a few blocks below for water
                        for (int checkY = surfaceY; checkY >= surfaceY - 3 && checkY >= world.getBottomY(); checkY--) {
                            mutable.set(worldX, checkY, worldZ);
                            BlockState checkBlock = world.getBlockState(mutable);

                            if (checkBlock.getBlock() == Blocks.WATER ||
                                    checkBlock.getBlock() instanceof FluidBlock ||
                                    !checkBlock.getFluidState().isEmpty()) {
                                foundWater = true;
                                break;
                            }

                            // If we hit a solid non-transparent block, stop looking
                            if (!checkBlock.isAir() && checkBlock.isOpaque()) {
                                break;
                            }
                        }

                        waterBlocks[mapZ * 128 + mapX] = foundWater;
                    }
                }

                // Second pass: determine colors based on actual water block patterns
                for (int mapX = 1; mapX < 127; ++mapX) {
                    for (int mapZ = 1; mapZ < 127; ++mapZ) {
                        int waterNeighbors = 0;

                        // Check 3x3 neighborhood for water blocks
                        for (int dx = -1; dx <= 1; ++dx) {
                            for (int dz = -1; dz <= 1; ++dz) {
                                if ((dx != 0 || dz != 0) && isWaterBlock(waterBlocks, mapX + dx, mapZ + dz)) {
                                    ++waterNeighbors;
                                }
                            }
                        }

                        MapColor.Brightness brightness = MapColor.Brightness.LOWEST;
                        MapColor mapColor = MapColor.CLEAR;

                        if (isWaterBlock(waterBlocks, mapX, mapZ)) {
                            mapColor = MapColor.ORANGE;
                            if (waterNeighbors > 7 && mapZ % 2 == 0) {
                                switch ((mapX + (int) (MathHelper.sin((float) mapZ + 0.0F) * 7.0F)) / 8 % 5) {
                                    case 0:
                                    case 4:
                                        brightness = MapColor.Brightness.LOW;
                                        break;
                                    case 1:
                                    case 3:
                                        brightness = MapColor.Brightness.NORMAL;
                                        break;
                                    case 2:
                                        brightness = MapColor.Brightness.HIGH;
                                }
                            } else if (waterNeighbors > 7) {
                                mapColor = MapColor.CLEAR;
                            } else if (waterNeighbors > 5) {
                                brightness = MapColor.Brightness.NORMAL;
                            } else if (waterNeighbors > 3) {
                                brightness = MapColor.Brightness.LOW;
                            } else if (waterNeighbors > 1) {
                                brightness = MapColor.Brightness.LOW;
                            }
                        } else if (waterNeighbors > 0) {
                            mapColor = MapColor.BROWN;
                            if (waterNeighbors > 3) {
                                brightness = MapColor.Brightness.NORMAL;
                            } else {
                                brightness = MapColor.Brightness.LOWEST;
                            }
                        }

                        if (mapColor != MapColor.CLEAR) {
                            mapState.setColor(mapX, mapZ, mapColor.getRenderColorByte(brightness));
                        }
                    }
                }
            }
        }
    }

    // Helper method for checking water blocks with correct array indexing
    private static boolean isWaterBlock(boolean[] waterBlocks, int mapX, int mapZ) {
        return waterBlocks[mapZ * 128 + mapX];
    }
}
