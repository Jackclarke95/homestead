package jackclarke95.homestead.block;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.custom.CuringVatBlock;
import jackclarke95.homestead.block.custom.FruitBearingLeaves;
import jackclarke95.homestead.block.custom.GenericBerryBushBlock;
import jackclarke95.homestead.block.custom.SpreadingBerryBushBlock;
import jackclarke95.homestead.block.custom.HeatedRackBlock;
import jackclarke95.homestead.block.custom.RackBlock;
import jackclarke95.homestead.block.custom.TroughBlock;
import jackclarke95.homestead.block.custom.VerticleSlabBlock;
import jackclarke95.homestead.world.tree.ModSaplingGenerators;
import jackclarke95.homestead.block.custom.MillBlock;
import jackclarke95.homestead.block.custom.PathBlock;
import jackclarke95.homestead.block.custom.SurfaceLayerConnectingBlock;
import jackclarke95.homestead.block.custom.GrowableSurfaceLayerBlock;
import jackclarke95.homestead.block.custom.HamperBlock;
import jackclarke95.homestead.block.custom.RhubarbBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import jackclarke95.homestead.block.custom.PressBlock;
import jackclarke95.homestead.block.custom.SowingBedBlock;

public class ModBlocks {
    public static final Block COARSE_DIRT_PATH = registerBlock("coarse_dirt_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.COARSE_DIRT).nonOpaque()));
    public static final Block PRESS = registerBlock("press",
            new PressBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block TROUGH = registerBlock("trough",
            new TroughBlock(AbstractBlock.Settings.copy(Blocks.CAULDRON)));
    public static final Block RACK = registerBlock("rack",
            new RackBlock(AbstractBlock.Settings.copy(Blocks.COMPOSTER).nonOpaque()));
    public static final Block HEATED_RACK = registerBlock("heated_rack",
            new HeatedRackBlock(AbstractBlock.Settings.copy(ModBlocks.RACK)
                    .luminance(state -> state.get(HeatedRackBlock.LIT) ? 15 : 0)));
    public static final Block CURING_VAT = registerBlock("curing_vat",
            new CuringVatBlock(AbstractBlock.Settings.copy(Blocks.COMPOSTER).nonOpaque()));
    public static final Block MILL = registerBlock("mill",
            new MillBlock(AbstractBlock.Settings.copy(Blocks.STONE)));
    public static final Block HAMPER = registerBlock("hamper",
            new HamperBlock(AbstractBlock.Settings.copy(Blocks.CHEST).nonOpaque()), new Item.Settings().maxCount(1));
    public static final Block SOWING_BED = registerBlock("sowing_bed",
            new SowingBedBlock(AbstractBlock.Settings.copy(Blocks.DIRT).nonOpaque()));
    public static final Block SAWDUST = registerBlock("sawdust",
            new SurfaceLayerConnectingBlock(
                    AbstractBlock.Settings.copy(Blocks.BROWN_CONCRETE_POWDER).nonOpaque()));

    public static final Block PEBBLE = registerBlock("pebble",
            new GrowableSurfaceLayerBlock(
                    AbstractBlock.Settings.copy(Blocks.COBBLESTONE).strength(0.5f).nonOpaque()));

    public static final Block COBBLESTONE_BRICKS = registerBlock("cobblestone_bricks",
            new Block((AbstractBlock.Settings.copy(Blocks.COBBLESTONE))));
    public static final Block COBBLESTONE_BRICK_SLAB = registerBlock("cobblestone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS)));
    public static final Block COBBLESTONE_BRICK_WALL = registerBlock("cobblestone_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS)));
    public static final Block COBBLESTONE_BRICK_STAIRS = registerBlock("cobblestone_brick_stairs",
            new StairsBlock(ModBlocks.COBBLESTONE_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS)));

    public static final Block THATCHED_HAY_BLOCK = registerBlock("thatched_hay_block",
            new Block(AbstractBlock.Settings.copy(Blocks.HAY_BLOCK)));
    public static final Block THATCHED_HAY_STAIRS = registerBlock("thatched_hay_stairs",
            new StairsBlock(Blocks.HAY_BLOCK.getDefaultState(),
                    AbstractBlock.Settings.copy(Blocks.HAY_BLOCK)));
    public static final Block THATCHED_HAY_SLAB = registerBlock("thatched_hay_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.HAY_BLOCK)));

    public static final Block WATTLE_AND_DAUB = registerBlock("wattle_and_daub",
            new VerticleSlabBlock(AbstractBlock.Settings.copy(Blocks.BRICKS).nonOpaque()));

    public static final Block STONE_PATH = registerBlock("stone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block COBBLESTONE_PATH = registerBlock("cobblestone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.STONE).nonOpaque()));
    public static final Block COBBLESTONE_BRICK_PATH = registerBlock("cobblestone_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS).nonOpaque()));
    public static final Block ANDESITE_PATH = registerBlock("andesite_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.ANDESITE).nonOpaque()));
    public static final Block POLISHED_ANDESITE_PATH = registerBlock("polished_andesite_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_ANDESITE).nonOpaque()));
    public static final Block DIORITE_PATH = registerBlock("diorite_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.DIORITE).nonOpaque()));
    public static final Block POLISHED_DIORITE_PATH = registerBlock("polished_diorite_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_DIORITE).nonOpaque()));
    public static final Block GRANITE_PATH = registerBlock("granite_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.GRANITE).nonOpaque()));
    public static final Block POLISHED_GRANITE_PATH = registerBlock("polished_granite_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_GRANITE).nonOpaque()));
    public static final Block SAND_PATH = registerBlock("sand_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SAND).nonOpaque()));
    public static final Block GRAVEL_PATH = registerBlock("gravel_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.GRAVEL).nonOpaque()));
    public static final Block OAK_PLANK_PATH = registerBlock("oak_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).nonOpaque()));
    public static final Block SPRUCE_PLANK_PATH = registerBlock("spruce_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS).nonOpaque()));
    public static final Block BIRCH_PLANK_PATH = registerBlock("birch_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.BIRCH_PLANKS).nonOpaque()));
    public static final Block JUNGLE_PLANK_PATH = registerBlock("jungle_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.JUNGLE_PLANKS).nonOpaque()));
    public static final Block ACACIA_PLANK_PATH = registerBlock("acacia_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.ACACIA_PLANKS).nonOpaque()));
    public static final Block DARK_OAK_PLANK_PATH = registerBlock("dark_oak_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.DARK_OAK_PLANKS).nonOpaque()));
    public static final Block MANGROVE_PLANK_PATH = registerBlock("mangrove_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.MANGROVE_PLANKS).nonOpaque()));
    public static final Block CHERRY_PLANK_PATH = registerBlock("cherry_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.CHERRY_PLANKS).nonOpaque()));
    public static final Block BAMBOO_PLANK_PATH = registerBlock("bamboo_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.BAMBOO_PLANKS).nonOpaque()));
    public static final Block CRIMSON_PLANK_PATH = registerBlock("crimson_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_PLANKS).nonOpaque()));
    public static final Block WARPED_PLANK_PATH = registerBlock("warped_plank_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.WARPED_PLANKS).nonOpaque()));
    public static final Block MOSSY_COBBLESTONE_PATH = registerBlock("mossy_cobblestone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.MOSSY_COBBLESTONE).nonOpaque()));
    public static final Block STONE_BRICK_PATH = registerBlock("stone_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICKS).nonOpaque()));
    public static final Block MOSSY_STONE_BRICK_PATH = registerBlock("mossy_stone_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.MOSSY_STONE_BRICKS).nonOpaque()));
    public static final Block SMOOTH_STONE_PATH = registerBlock("smooth_stone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE).nonOpaque()));
    public static final Block CHISELED_STONE_BRICK_PATH = registerBlock("chiseled_stone_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_STONE_BRICKS).nonOpaque()));
    public static final Block DEEPSLATE_PATH = registerBlock("deepslate_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).nonOpaque()));
    public static final Block COBBLED_DEEPSLATE_PATH = registerBlock("cobbled_deepslate_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE).nonOpaque()));
    public static final Block DEEPSLATE_BRICK_PATH = registerBlock("deepslate_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_BRICKS).nonOpaque()));
    public static final Block DEEPSLATE_TILE_PATH = registerBlock("deepslate_tile_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_TILES).nonOpaque()));
    public static final Block POLISHED_DEEPSLATE_PATH = registerBlock("polished_deepslate_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_DEEPSLATE).nonOpaque()));
    public static final Block POLISHED_TUFF_PATH = registerBlock("polished_tuff_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF).nonOpaque()));
    public static final Block TUFF_BRICK_PATH = registerBlock("tuff_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS).nonOpaque()));
    public static final Block BRICKS_PATH = registerBlock("bricks_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.BRICKS).nonOpaque()));
    public static final Block PACKED_MUD_PATH = registerBlock("packed_mud_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.PACKED_MUD).nonOpaque()));
    public static final Block MUD_BRICK_PATH = registerBlock("mud_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.MUD_BRICKS).nonOpaque()));
    public static final Block SMOOTH_SANDSTONE_PATH = registerBlock("smooth_sandstone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_SANDSTONE).nonOpaque()));
    public static final Block SMOOTH_RED_SANDSTONE_PATH = registerBlock("smooth_red_sandstone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_RED_SANDSTONE).nonOpaque()));
    public static final Block PRISMARINE_PATH = registerBlock("prismarine_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.PRISMARINE).nonOpaque()));
    public static final Block PRISMARINE_BRICK_PATH = registerBlock("prismarine_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.PRISMARINE_BRICKS).nonOpaque()));
    public static final Block DARK_PRISMARINE_PATH = registerBlock("dark_prismarine_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.DARK_PRISMARINE).nonOpaque()));
    public static final Block NETHERRACK_PATH = registerBlock("netherrack_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.NETHERRACK).nonOpaque()));
    public static final Block NETHER_BRICK_PATH = registerBlock("nether_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.NETHER_BRICKS).nonOpaque()));
    public static final Block CHISELED_NETHER_BRICK_PATH = registerBlock("chiseled_nether_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_NETHER_BRICKS).nonOpaque()));
    public static final Block RED_NETHER_BRICK_PATH = registerBlock("red_nether_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.RED_NETHER_BRICKS).nonOpaque()));
    public static final Block SMOOTH_BASALT_PATH = registerBlock("smooth_basalt_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_BASALT).nonOpaque()));
    public static final Block BLACKSTONE_PATH = registerBlock("blackstone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.BLACKSTONE).nonOpaque()));
    public static final Block GILDED_BLACKSTONE_PATH = registerBlock("gilded_blackstone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.GILDED_BLACKSTONE).nonOpaque()));
    public static final Block CHISELED_POLISHED_BLACKSTONE_PATH = registerBlock("chiseled_polished_blackstone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_POLISHED_BLACKSTONE).nonOpaque()));
    public static final Block POLISHED_BLACKSTONE_PATH = registerBlock("polished_blackstone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_BLACKSTONE).nonOpaque()));
    public static final Block POLISHED_BLACKSTONE_BRICK_PATH = registerBlock("polished_blackstone_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_BLACKSTONE_BRICKS).nonOpaque()));
    public static final Block END_STONE_PATH = registerBlock("end_stone_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.END_STONE).nonOpaque()));
    public static final Block END_STONE_BRICK_PATH = registerBlock("end_stone_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.END_STONE_BRICKS).nonOpaque()));
    public static final Block PURPUR_BLOCK_PATH = registerBlock("purpur_block_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.PURPUR_BLOCK).nonOpaque()));
    public static final Block PURPUR_PILLAR_PATH = registerBlock("purpur_pillar_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.PURPUR_PILLAR).nonOpaque()));
    public static final Block QUARTZ_BLOCK_PATH = registerBlock("quartz_block_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.QUARTZ_BLOCK).nonOpaque()));
    public static final Block CHISELED_QUARTZ_BLOCK_PATH = registerBlock("chiseled_quartz_block_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_QUARTZ_BLOCK).nonOpaque()));
    public static final Block QUARTZ_BRICK_PATH = registerBlock("quartz_brick_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.QUARTZ_BRICKS).nonOpaque()));
    public static final Block SMOOTH_QUARTZ_BLOCK_PATH = registerBlock("smooth_quartz_block_path",
            new PathBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_QUARTZ).nonOpaque()));

    public static final Block PEAR_TREE_LOG = registerBlock("pear_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block PEAR_TREE_WOOD = registerBlock("pear_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_PEAR_TREE_LOG = registerBlock("stripped_pear_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_PEAR_TREE_WOOD = registerBlock("stripped_pear_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block PEAR_TREE_PLANKS = registerBlock("pear_tree_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block PEAR_TREE_LEAVES = registerBlock("pear_tree_leaves",
            new FruitBearingLeaves(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block PEAR_TREE_SAPLING = registerBlock("pear_tree_sapling",
            new SaplingBlock(ModSaplingGenerators.PEAR_TREE,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block APPLE_TREE_LOG = registerBlock("apple_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block APPLE_TREE_WOOD = registerBlock("apple_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_APPLE_TREE_LOG = registerBlock("stripped_apple_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_APPLE_TREE_WOOD = registerBlock("stripped_apple_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block APPLE_TREE_PLANKS = registerBlock("apple_tree_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block APPLE_TREE_LEAVES = registerBlock("apple_tree_leaves",
            new FruitBearingLeaves(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block APPLE_TREE_SAPLING = registerBlock("apple_tree_sapling",
            new SaplingBlock(ModSaplingGenerators.APPLE_TREE,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block PLUM_TREE_LOG = registerBlock("plum_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block PLUM_TREE_WOOD = registerBlock("plum_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_PLUM_TREE_LOG = registerBlock("stripped_plum_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_PLUM_TREE_WOOD = registerBlock("stripped_plum_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block PLUM_TREE_PLANKS = registerBlock("plum_tree_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block PLUM_TREE_LEAVES = registerBlock("plum_tree_leaves",
            new FruitBearingLeaves(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block PLUM_TREE_SAPLING = registerBlock("plum_tree_sapling",
            new SaplingBlock(ModSaplingGenerators.PLUM_TREE,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block LEMON_TREE_LOG = registerBlock("lemon_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block LEMON_TREE_WOOD = registerBlock("lemon_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_LEMON_TREE_LOG = registerBlock("stripped_lemon_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_LEMON_TREE_WOOD = registerBlock("stripped_lemon_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block LEMON_TREE_PLANKS = registerBlock("lemon_tree_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block LEMON_TREE_LEAVES = registerBlock("lemon_tree_leaves",
            new FruitBearingLeaves(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block LEMON_TREE_SAPLING = registerBlock("lemon_tree_sapling",
            new SaplingBlock(ModSaplingGenerators.LEMON_TREE,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block ORANGE_TREE_LOG = registerBlock("orange_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block ORANGE_TREE_WOOD = registerBlock("orange_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_ORANGE_TREE_LOG = registerBlock("stripped_orange_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_ORANGE_TREE_WOOD = registerBlock("stripped_orange_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block ORANGE_TREE_PLANKS = registerBlock("orange_tree_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block ORANGE_TREE_LEAVES = registerBlock("orange_tree_leaves",
            new FruitBearingLeaves(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block ORANGE_TREE_SAPLING = registerBlock("orange_tree_sapling",
            new SaplingBlock(ModSaplingGenerators.ORANGE_TREE,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block PEACH_TREE_LOG = registerBlock("peach_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block PEACH_TREE_WOOD = registerBlock("peach_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_PEACH_TREE_LOG = registerBlock("stripped_peach_tree_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_PEACH_TREE_WOOD = registerBlock("stripped_peach_tree_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block PEACH_TREE_PLANKS = registerBlock("peach_tree_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block PEACH_TREE_LEAVES = registerBlock("peach_tree_leaves",
            new FruitBearingLeaves(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block PEACH_TREE_SAPLING = registerBlock("peach_tree_sapling",
            new SaplingBlock(ModSaplingGenerators.PEACH_TREE,
                    AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static GenericBerryBushBlock BLACKBERRY_BUSH = (GenericBerryBushBlock) registerBlock("blackberry_bush",
            new GenericBerryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH), null));
    public static GenericBerryBushBlock RASPBERRY_BUSH = (GenericBerryBushBlock) registerBlock("raspberry_bush",
            new GenericBerryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH), null));

    public static SpreadingBerryBushBlock STRAWBERRY_BUSH = (SpreadingBerryBushBlock) registerBlock("strawberry_bush",
            new SpreadingBerryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH), null, false));

    public static final Block RHUBARB_CROP = Registry.register(Registries.BLOCK,
            Identifier.of(Homestead.MOD_ID, "rhubarb"),
            new RhubarbBlock(AbstractBlock.Settings.copy(Blocks.BEETROOTS)));

    public static void registerModBlocks() {
        Homestead.LOGGER.debug("Registering Mod Blocks for " + Homestead.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.COBBLESTONE_BRICKS);
            entries.add(ModBlocks.COBBLESTONE_BRICK_SLAB);
            entries.add(ModBlocks.COBBLESTONE_BRICK_WALL);
            entries.add(ModBlocks.COBBLESTONE_BRICK_STAIRS);
            entries.add(ModBlocks.THATCHED_HAY_STAIRS);
            entries.add(ModBlocks.THATCHED_HAY_SLAB);
            entries.add(ModBlocks.THATCHED_HAY_BLOCK);
            entries.add(ModBlocks.WATTLE_AND_DAUB);
            entries.add(ModBlocks.STONE_PATH);
            entries.add(ModBlocks.COBBLESTONE_PATH);
            entries.add(ModBlocks.COBBLESTONE_BRICK_PATH);
            entries.add(ModBlocks.ANDESITE_PATH);
            entries.add(ModBlocks.POLISHED_ANDESITE_PATH);
            entries.add(ModBlocks.DIORITE_PATH);
            entries.add(ModBlocks.POLISHED_DIORITE_PATH);
            entries.add(ModBlocks.GRANITE_PATH);
            entries.add(ModBlocks.POLISHED_GRANITE_PATH);
            entries.add(ModBlocks.SAND_PATH);
            entries.add(ModBlocks.GRAVEL_PATH);
            entries.add(ModBlocks.OAK_PLANK_PATH);
            entries.add(ModBlocks.SPRUCE_PLANK_PATH);
            entries.add(ModBlocks.BIRCH_PLANK_PATH);
            entries.add(ModBlocks.JUNGLE_PLANK_PATH);
            entries.add(ModBlocks.ACACIA_PLANK_PATH);
            entries.add(ModBlocks.DARK_OAK_PLANK_PATH);
            entries.add(ModBlocks.MANGROVE_PLANK_PATH);
            entries.add(ModBlocks.CHERRY_PLANK_PATH);
            entries.add(ModBlocks.BAMBOO_PLANK_PATH);
            entries.add(ModBlocks.CRIMSON_PLANK_PATH);
            entries.add(ModBlocks.WARPED_PLANK_PATH);
            entries.add(ModBlocks.MOSSY_COBBLESTONE_PATH);
            entries.add(ModBlocks.STONE_BRICK_PATH);
            entries.add(ModBlocks.MOSSY_STONE_BRICK_PATH);
            entries.add(ModBlocks.SMOOTH_STONE_PATH);
            entries.add(ModBlocks.CHISELED_STONE_BRICK_PATH);
            entries.add(ModBlocks.DEEPSLATE_PATH);
            entries.add(ModBlocks.COBBLED_DEEPSLATE_PATH);
            entries.add(ModBlocks.DEEPSLATE_BRICK_PATH);
            entries.add(ModBlocks.DEEPSLATE_TILE_PATH);
            entries.add(ModBlocks.POLISHED_DEEPSLATE_PATH);
            entries.add(ModBlocks.POLISHED_TUFF_PATH);
            entries.add(ModBlocks.TUFF_BRICK_PATH);
            entries.add(ModBlocks.BRICKS_PATH);
            entries.add(ModBlocks.PACKED_MUD_PATH);
            entries.add(ModBlocks.MUD_BRICK_PATH);
            entries.add(ModBlocks.SMOOTH_SANDSTONE_PATH);
            entries.add(ModBlocks.SMOOTH_RED_SANDSTONE_PATH);
            entries.add(ModBlocks.PRISMARINE_PATH);
            entries.add(ModBlocks.PRISMARINE_BRICK_PATH);
            entries.add(ModBlocks.DARK_PRISMARINE_PATH);
            entries.add(ModBlocks.NETHERRACK_PATH);
            entries.add(ModBlocks.NETHER_BRICK_PATH);
            entries.add(ModBlocks.CHISELED_NETHER_BRICK_PATH);
            entries.add(ModBlocks.RED_NETHER_BRICK_PATH);
            entries.add(ModBlocks.SMOOTH_BASALT_PATH);
            entries.add(ModBlocks.BLACKSTONE_PATH);
            entries.add(ModBlocks.GILDED_BLACKSTONE_PATH);
            entries.add(ModBlocks.CHISELED_POLISHED_BLACKSTONE_PATH);
            entries.add(ModBlocks.POLISHED_BLACKSTONE_PATH);
            entries.add(ModBlocks.POLISHED_BLACKSTONE_BRICK_PATH);
            entries.add(ModBlocks.END_STONE_PATH);
            entries.add(ModBlocks.END_STONE_BRICK_PATH);
            entries.add(ModBlocks.PURPUR_BLOCK_PATH);
            entries.add(ModBlocks.PURPUR_PILLAR_PATH);
            entries.add(ModBlocks.QUARTZ_BLOCK_PATH);
            entries.add(ModBlocks.CHISELED_QUARTZ_BLOCK_PATH);
            entries.add(ModBlocks.QUARTZ_BRICK_PATH);
            entries.add(ModBlocks.SMOOTH_QUARTZ_BLOCK_PATH);
            entries.add(ModBlocks.COARSE_DIRT_PATH);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(ModBlocks.APPLE_TREE_SAPLING);
            entries.add(ModBlocks.APPLE_TREE_LOG);
            entries.add(ModBlocks.APPLE_TREE_LEAVES);
            entries.add(ModBlocks.APPLE_TREE_PLANKS);
            entries.add(ModBlocks.STRIPPED_APPLE_TREE_LOG);
            entries.add(ModBlocks.STRIPPED_APPLE_TREE_WOOD);
            entries.add(ModBlocks.PEAR_TREE_SAPLING);
            entries.add(ModBlocks.PEAR_TREE_LOG);
            entries.add(ModBlocks.PEAR_TREE_LEAVES);
            entries.add(ModBlocks.PEAR_TREE_PLANKS);
            entries.add(ModBlocks.STRIPPED_PEAR_TREE_LOG);
            entries.add(ModBlocks.STRIPPED_PEAR_TREE_WOOD);
            entries.add(ModBlocks.PLUM_TREE_SAPLING);
            entries.add(ModBlocks.PLUM_TREE_LOG);
            entries.add(ModBlocks.PLUM_TREE_LEAVES);
            entries.add(ModBlocks.PLUM_TREE_PLANKS);
            entries.add(ModBlocks.STRIPPED_PLUM_TREE_LOG);
            entries.add(ModBlocks.STRIPPED_PLUM_TREE_WOOD);
            entries.add(ModBlocks.LEMON_TREE_SAPLING);
            entries.add(ModBlocks.LEMON_TREE_LOG);
            entries.add(ModBlocks.LEMON_TREE_LEAVES);
            entries.add(ModBlocks.LEMON_TREE_PLANKS);
            entries.add(ModBlocks.STRIPPED_LEMON_TREE_LOG);
            entries.add(ModBlocks.STRIPPED_LEMON_TREE_WOOD);
            entries.add(ModBlocks.ORANGE_TREE_SAPLING);
            entries.add(ModBlocks.ORANGE_TREE_LOG);
            entries.add(ModBlocks.ORANGE_TREE_LEAVES);
            entries.add(ModBlocks.ORANGE_TREE_PLANKS);
            entries.add(ModBlocks.STRIPPED_ORANGE_TREE_LOG);
            entries.add(ModBlocks.STRIPPED_ORANGE_TREE_WOOD);
            entries.add(ModBlocks.PEACH_TREE_SAPLING);
            entries.add(ModBlocks.PEACH_TREE_LOG);
            entries.add(ModBlocks.PEACH_TREE_LEAVES);
            entries.add(ModBlocks.PEACH_TREE_PLANKS);
            entries.add(ModBlocks.STRIPPED_PEACH_TREE_LOG);
            entries.add(ModBlocks.STRIPPED_PEACH_TREE_WOOD);
            entries.add(ModBlocks.BLACKBERRY_BUSH);
            entries.add(ModBlocks.RASPBERRY_BUSH);
            entries.add(ModBlocks.STRAWBERRY_BUSH);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.RACK);
            entries.add(ModBlocks.HEATED_RACK);
            entries.add(ModBlocks.CURING_VAT);
            entries.add(ModBlocks.TROUGH);
            entries.add(ModBlocks.MILL);
            entries.add(ModBlocks.PRESS);
            entries.add(ModBlocks.SOWING_BED);
            entries.add(ModBlocks.HAMPER);
        });
    }

    private static void registerBlockItem(String name, Block block, Item.Settings settings) {
        Registry.register(
                Registries.ITEM, Identifier.of(Homestead.MOD_ID, name),
                new BlockItem(block, settings));
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(
                Registries.ITEM, Identifier.of(Homestead.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);

        return Registry.register(Registries.BLOCK, Identifier.of(Homestead.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, Item.Settings settings) {
        registerBlockItem(name, block, settings);

        return Registry.register(Registries.BLOCK, Identifier.of(Homestead.MOD_ID, name), block);
    }
}
