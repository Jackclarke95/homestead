package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.item.ModItems;
import jackclarke95.homestead.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

        public ModItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
                super(output, completableFuture);
        }

        @Override
        protected void configure(WrapperLookup wrapperLookup) {
                getOrCreateTagBuilder(ModTags.ItemTags.ROOT_VEGETABLES)
                                .add(Items.CARROT)
                                .add(Items.POTATO)
                                .add(Items.BEETROOT);

                getOrCreateTagBuilder(ModTags.ItemTags.CROP_SEEDS)
                                .add(Items.WHEAT_SEEDS)
                                .add(Items.BEETROOT_SEEDS)
                                .add(Items.PUMPKIN_SEEDS)
                                .add(Items.MELON_SEEDS);

                getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN)
                                .add(ModBlocks.PEAR_TREE_LOG.asItem())
                                .add(ModBlocks.PEAR_TREE_WOOD.asItem())
                                .add(ModBlocks.STRIPPED_PEAR_TREE_LOG.asItem())
                                .add(ModBlocks.STRIPPED_PEAR_TREE_WOOD.asItem())
                                .add(ModBlocks.PLUM_TREE_LOG.asItem())
                                .add(ModBlocks.PLUM_TREE_WOOD.asItem())
                                .add(ModBlocks.STRIPPED_PLUM_TREE_LOG.asItem())
                                .add(ModBlocks.STRIPPED_PLUM_TREE_WOOD.asItem())
                                .add(ModBlocks.LEMON_TREE_LOG.asItem())
                                .add(ModBlocks.LEMON_TREE_WOOD.asItem())
                                .add(ModBlocks.STRIPPED_LEMON_TREE_LOG.asItem())
                                .add(ModBlocks.STRIPPED_LEMON_TREE_WOOD.asItem())
                                .add(ModBlocks.ORANGE_TREE_LOG.asItem())
                                .add(ModBlocks.ORANGE_TREE_WOOD.asItem())
                                .add(ModBlocks.STRIPPED_ORANGE_TREE_LOG.asItem())
                                .add(ModBlocks.STRIPPED_ORANGE_TREE_WOOD.asItem())
                                .add(ModBlocks.APRICOT_TREE_LOG.asItem())
                                .add(ModBlocks.APRICOT_TREE_WOOD.asItem())
                                .add(ModBlocks.STRIPPED_APRICOT_TREE_LOG.asItem())
                                .add(ModBlocks.STRIPPED_APRICOT_TREE_WOOD.asItem())
                                .add(ModBlocks.PEACH_TREE_LOG.asItem())
                                .add(ModBlocks.PEACH_TREE_WOOD.asItem())
                                .add(ModBlocks.STRIPPED_PEACH_TREE_LOG.asItem())
                                .add(ModBlocks.STRIPPED_PEACH_TREE_WOOD.asItem());

                getOrCreateTagBuilder(ItemTags.PLANKS)
                                .add(ModBlocks.PEAR_TREE_PLANKS.asItem())
                                .add(ModBlocks.PLUM_TREE_PLANKS.asItem())
                                .add(ModBlocks.LEMON_TREE_PLANKS.asItem())
                                .add(ModBlocks.ORANGE_TREE_PLANKS.asItem())
                                .add(ModBlocks.APRICOT_TREE_PLANKS.asItem())
                                .add(ModBlocks.PEACH_TREE_PLANKS.asItem());

                getOrCreateTagBuilder(ModTags.ItemTags.FRUITS)
                                .add(Items.APPLE)
                                .add(ModItems.PEAR)
                                .add(ModItems.PLUM)
                                .add(ModItems.LEMON)
                                .add(ModItems.ORANGE)
                                .add(ModItems.APRICOT)
                                .add(ModItems.PEACH)
                                .add(ModItems.BLACKBERRY)
                                .add(ModItems.RASPBERRY);
        }
}
