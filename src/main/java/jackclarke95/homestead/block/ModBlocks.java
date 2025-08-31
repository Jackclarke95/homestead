package jackclarke95.homestead.block;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.custom.CuringVatBlock;
import jackclarke95.homestead.block.custom.HeatedRackBlock;
import jackclarke95.homestead.block.custom.RackBlock;
import jackclarke95.homestead.block.custom.TroughBlock;
import jackclarke95.homestead.block.custom.MillBlock;
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

        public static final Block CURING_VAT = registerBlock("curing_vat",
                        new CuringVatBlock(AbstractBlock.Settings.copy(Blocks.COMPOSTER).nonOpaque()));

        public static final Block MILL = registerBlock("mill",
                        new MillBlock(AbstractBlock.Settings.copy(Blocks.STONE)));

        public static void registerModBlocks() {
                Homestead.LOGGER.info("Registering Mod Blocks for " + Homestead.MOD_ID);

                ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
                        entries.add(ModBlocks.COBBLESTONE_BRICKS);
                        entries.add(ModBlocks.COBBLESTONE_BRICK_SLAB);
                        entries.add(ModBlocks.COBBLESTONE_BRICK_WALL);
                        entries.add(ModBlocks.COBBLESTONE_BRICK_STAIRS);
                        entries.add(ModBlocks.HAY_STAIRS);
                        entries.add(ModBlocks.PRESS);
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
