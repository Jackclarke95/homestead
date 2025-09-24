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
        // Add all vanilla food items to custom FOODS tag
        getOrCreateTagBuilder(ModTags.ItemTags.FOODS)
                .add(Items.APPLE)
                .add(Items.BAKED_POTATO)
                .add(Items.BEETROOT)
                .add(Items.BEETROOT_SOUP)
                .add(Items.BREAD)
                .add(Items.CAKE)
                .add(Items.CARROT)
                .add(Items.CHORUS_FRUIT)
                .add(Items.COOKED_BEEF)
                .add(Items.COOKED_CHICKEN)
                .add(Items.COOKED_COD)
                .add(Items.COOKED_MUTTON)
                .add(Items.COOKED_PORKCHOP)
                .add(Items.COOKED_RABBIT)
                .add(Items.COOKED_SALMON)
                .add(Items.COOKIE)
                .add(Items.DRIED_KELP)
                .add(Items.ENCHANTED_GOLDEN_APPLE)
                .add(Items.GLOW_BERRIES)
                .add(Items.GOLDEN_APPLE)
                .add(Items.GOLDEN_CARROT)
                .add(Items.HONEY_BOTTLE)
                .add(Items.MELON_SLICE)
                .add(Items.MUSHROOM_STEW)
                .add(Items.POISONOUS_POTATO)
                .add(Items.POTATO)
                .add(Items.PUFFERFISH)
                .add(Items.PUMPKIN_PIE)
                .add(Items.RABBIT_STEW)
                .add(Items.BEEF)
                .add(Items.CHICKEN)
                .add(Items.COD)
                .add(Items.MUTTON)
                .add(Items.PORKCHOP)
                .add(Items.RABBIT)
                .add(Items.SALMON)
                .add(Items.ROTTEN_FLESH)
                .add(Items.SPIDER_EYE)
                .add(Items.SUSPICIOUS_STEW)
                .add(Items.SWEET_BERRIES)
                .add(Items.TROPICAL_FISH)

                .add(ModItems.CHEESE_SLICE)
                .add(ModItems.SUSPICIOUS_JERKY)
                .add(ModItems.BEEF_JERKY)
                .add(ModItems.PORK_JERKY)
                .add(ModItems.MUTTON_JERKY)
                .add(ModItems.CHICKEN_JERKY)
                .add(ModItems.RABBIT_JERKY)
                .add(ModItems.COD_JERKY)
                .add(ModItems.SALMON_JERKY)
                .add(ModItems.PEAR)
                .add(ModItems.PLUM)
                .add(ModItems.LEMON)
                .add(ModItems.ORANGE)
                .add(ModItems.APRICOT)
                .add(ModItems.PEACH)
                .add(ModItems.BLACKBERRY)
                .add(ModItems.RASPBERRY)
                .add(ModItems.MUG_OF_VODKA)
                .add(ModItems.SOFT_CHEESE);

        // Add all jerky items to vanilla MEAT tag
        getOrCreateTagBuilder(ItemTags.MEAT)
                .add(ModItems.BEEF_JERKY)
                .add(ModItems.PORK_JERKY)
                .add(ModItems.MUTTON_JERKY)
                .add(ModItems.CHICKEN_JERKY)
                .add(ModItems.RABBIT_JERKY)
                .add(ModItems.COD_JERKY)
                .add(ModItems.SALMON_JERKY)
                .add(ModItems.SUSPICIOUS_JERKY);

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
