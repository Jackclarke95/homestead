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
                ModBlocks.STONE_PATH);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(
                ModBlocks.RACK,
                ModBlocks.HEATED_RACK,
                ModBlocks.MILL,
                ModBlocks.CURING_VAT,
                ModBlocks.PRESS,
                ModBlocks.WATTLE_AND_DAUB);

        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE).add(
                ModBlocks.HAY_STAIRS);
    }

}
