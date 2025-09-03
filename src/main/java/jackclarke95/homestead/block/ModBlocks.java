package jackclarke95.homestead.block;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.custom.CuringVatBlock;
import jackclarke95.homestead.block.custom.HeatedRackBlock;
import jackclarke95.homestead.block.custom.RackBlock;
import jackclarke95.homestead.block.custom.TroughBlock;
import jackclarke95.homestead.block.custom.VerticleSlabBlock;
import jackclarke95.homestead.block.custom.MillBlock;
import jackclarke95.homestead.block.custom.PathBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

public class ModBlocks {
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

        public static final Block COBBLESTONE_BRICKS = registerBlock("cobblestone_bricks",
                        new Block((AbstractBlock.Settings.copy(Blocks.COBBLESTONE))));
        public static final Block COBBLESTONE_BRICK_SLAB = registerBlock("cobblestone_brick_slab",
                        new SlabBlock(AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS)));
        public static final Block COBBLESTONE_BRICK_WALL = registerBlock("cobblestone_brick_wall",
                        new WallBlock(AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS)));
        public static final Block COBBLESTONE_BRICK_STAIRS = registerBlock("cobblestone_brick_stairs",
                        new StairsBlock(ModBlocks.COBBLESTONE_BRICKS.getDefaultState(),
                                        AbstractBlock.Settings.copy(ModBlocks.COBBLESTONE_BRICKS)));

        public static final Block HAY_STAIRS = registerBlock("hay_stairs",
                        new StairsBlock(Blocks.HAY_BLOCK.getDefaultState(),
                                        AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE)));

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

        public static void registerModBlocks() {
                Homestead.LOGGER.info("Registering Mod Blocks for " + Homestead.MOD_ID);

                ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
                        entries.add(ModBlocks.COBBLESTONE_BRICKS);
                        entries.add(ModBlocks.COBBLESTONE_BRICK_SLAB);
                        entries.add(ModBlocks.COBBLESTONE_BRICK_WALL);
                        entries.add(ModBlocks.COBBLESTONE_BRICK_STAIRS);
                        entries.add(ModBlocks.HAY_STAIRS);
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
                });

                ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
                        entries.add(ModBlocks.RACK);
                        entries.add(ModBlocks.HEATED_RACK);
                        entries.add(ModBlocks.CURING_VAT);
                        entries.add(ModBlocks.TROUGH);
                        entries.add(ModBlocks.MILL);
                        entries.add(ModBlocks.PRESS);
                });
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
}
