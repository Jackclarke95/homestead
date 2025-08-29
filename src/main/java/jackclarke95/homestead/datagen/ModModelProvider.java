package jackclarke95.homestead.datagen;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.custom.CustomBlock;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {

        public ModModelProvider(FabricDataOutput output) {
                super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

                BlockStateModelGenerator.BlockTexturePool cobblestoneBricksPool = blockStateModelGenerator
                                .registerCubeAllModelTexturePool(ModBlocks.COBBLESTONE_BRICKS);

                cobblestoneBricksPool.stairs(ModBlocks.COBBLESTONE_BRICK_STAIRS);
                cobblestoneBricksPool.slab(ModBlocks.COBBLESTONE_BRICK_SLAB);

                cobblestoneBricksPool.wall(ModBlocks.COBBLESTONE_BRICK_WALL);

                Identifier blockOffIdentifier = TexturedModel.CUBE_ALL.upload(ModBlocks.CUSTOM_BLOCK,
                                blockStateModelGenerator.modelCollector);
                Identifier blockOnIdentifier = blockStateModelGenerator.createSubModel(ModBlocks.CUSTOM_BLOCK, "_on",
                                Models.CUBE_ALL, TextureMap::all);

                blockStateModelGenerator.blockStateCollector
                                .accept(VariantsBlockStateSupplier.create(ModBlocks.CUSTOM_BLOCK)
                                                .coordinate(BlockStateModelGenerator.createBooleanModelMap(
                                                                CustomBlock.CLICKED, blockOnIdentifier,
                                                                blockOffIdentifier)));

                blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RACK);
                blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.CURING_VAT);

                registerCubeBottomTop(blockStateModelGenerator, ModBlocks.MILL);

                final TextureMap hayTexture = TextureMap.all(Identifier.of(Homestead.MOD_ID, "block/hay_stairs"));

                final Identifier hayStairsModelId = Models.STAIRS.upload(ModBlocks.HAY_STAIRS, hayTexture,
                                blockStateModelGenerator.modelCollector);
                final Identifier hayInnerStairsModelId = Models.INNER_STAIRS.upload(ModBlocks.HAY_STAIRS, hayTexture,
                                blockStateModelGenerator.modelCollector);
                final Identifier hayOuterStairsModelId = Models.OUTER_STAIRS.upload(ModBlocks.HAY_STAIRS, hayTexture,
                                blockStateModelGenerator.modelCollector);

                blockStateModelGenerator.blockStateCollector.accept(
                                BlockStateModelGenerator.createStairsBlockState(
                                                ModBlocks.HAY_STAIRS,
                                                hayInnerStairsModelId,
                                                hayStairsModelId,
                                                hayOuterStairsModelId));
                blockStateModelGenerator.registerParentedItemModel(ModBlocks.HAY_STAIRS, hayStairsModelId);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
                itemModelGenerator.register(ModItems.RAW_HIDE, Models.GENERATED);
                itemModelGenerator.register(ModItems.MUG, Models.GENERATED);
                itemModelGenerator.register(ModItems.CHEESE_WHEEL, Models.GENERATED);
                itemModelGenerator.register(ModItems.CHEESE_SLICE, Models.GENERATED);
                itemModelGenerator.register(ModItems.SUSPICIOUS_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.BEEF_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.PORK_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.MUTTON_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.CHICKEN_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.RABBIT_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.COD_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.SALMON_JERKY, Models.GENERATED);
                itemModelGenerator.register(ModItems.MUG_OF_VODKA, Models.GENERATED);
                itemModelGenerator.register(ModItems.FLOUR, Models.GENERATED);
                itemModelGenerator.register(ModItems.SEED_MIX, Models.GENERATED);
                itemModelGenerator.register(ModItems.ANIMAL_FEED, Models.GENERATED);
        }

        private void registerCubeBottomTop(BlockStateModelGenerator blockStateModelGenerator,
                        Block block) {
                TextureMap textureMap = new TextureMap()
                                .put(TextureKey.TOP, TextureMap.getSubId(block, "_top"))
                                .put(TextureKey.SIDE, TextureMap.getSubId(block, "_side"))
                                .put(TextureKey.BOTTOM, TextureMap.getSubId(block, "_bottom"));

                Identifier modelId = Models.CUBE_BOTTOM_TOP.upload(block, textureMap,
                                blockStateModelGenerator.modelCollector);
                blockStateModelGenerator.blockStateCollector
                                .accept(BlockStateModelGenerator.createSingletonBlockState(block, modelId));
                blockStateModelGenerator.registerParentedItemModel(block, modelId);
        }
}
