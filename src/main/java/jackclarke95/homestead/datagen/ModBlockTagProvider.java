package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

        public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
                super(output, registriesFuture);
        }

        @Override
        protected void configure(WrapperLookup wrapperLookup) {
                getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
                                ModBlocks.TROUGH,
                                ModBlocks.COBBLESTONE_BRICKS,
                                ModBlocks.COBBLESTONE_BRICK_SLAB,
                                ModBlocks.COBBLESTONE_BRICK_STAIRS,
                                ModBlocks.COBBLESTONE_BRICK_WALL,
                                ModBlocks.STONE_PATH,
                                ModBlocks.PEBBLE);

                getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(
                                ModBlocks.RACK,
                                ModBlocks.HEATED_RACK,
                                ModBlocks.MILL,
                                ModBlocks.CURING_VAT,
                                ModBlocks.PRESS,
                                ModBlocks.WATTLE_AND_DAUB);

                getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(
                                ModBlocks.HAY_STAIRS);

                getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                                .add(ModBlocks.APPLE_TREE_LOG)
                                .add(ModBlocks.APPLE_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_APPLE_TREE_LOG)
                                .add(ModBlocks.STRIPPED_APPLE_TREE_WOOD)
                                .add(ModBlocks.PEAR_TREE_LOG)
                                .add(ModBlocks.PEAR_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_PEAR_TREE_LOG)
                                .add(ModBlocks.STRIPPED_PEAR_TREE_WOOD)
                                .add(ModBlocks.PLUM_TREE_LOG)
                                .add(ModBlocks.PLUM_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_PLUM_TREE_LOG)
                                .add(ModBlocks.STRIPPED_PLUM_TREE_WOOD)
                                .add(ModBlocks.LEMON_TREE_LOG)
                                .add(ModBlocks.LEMON_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_LEMON_TREE_LOG)
                                .add(ModBlocks.STRIPPED_LEMON_TREE_WOOD)
                                .add(ModBlocks.ORANGE_TREE_LOG)
                                .add(ModBlocks.ORANGE_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_ORANGE_TREE_LOG)
                                .add(ModBlocks.STRIPPED_ORANGE_TREE_WOOD)
                                .add(ModBlocks.APRICOT_TREE_LOG)
                                .add(ModBlocks.APRICOT_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_APRICOT_TREE_LOG)
                                .add(ModBlocks.STRIPPED_APRICOT_TREE_WOOD)
                                .add(ModBlocks.PEACH_TREE_LOG)
                                .add(ModBlocks.PEACH_TREE_WOOD)
                                .add(ModBlocks.STRIPPED_PEACH_TREE_LOG)
                                .add(ModBlocks.STRIPPED_PEACH_TREE_WOOD);
        }

}
