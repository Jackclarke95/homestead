package jackclarke95.homestead.datagen;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModModelProvider extends FabricModelProvider {
        private final FabricDataOutput output;

        public ModModelProvider(FabricDataOutput output) {
                super(output);
                this.output = output;
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

                BlockStateModelGenerator.BlockTexturePool cobblestoneBricksPool = blockStateModelGenerator
                                .registerCubeAllModelTexturePool(ModBlocks.COBBLESTONE_BRICKS);

                cobblestoneBricksPool.stairs(ModBlocks.COBBLESTONE_BRICK_STAIRS);
                cobblestoneBricksPool.slab(ModBlocks.COBBLESTONE_BRICK_SLAB);

                cobblestoneBricksPool.wall(ModBlocks.COBBLESTONE_BRICK_WALL);

                blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RACK);
                blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.CURING_VAT);
                blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.PRESS);

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

                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.STONE_PATH,
                                Blocks.STONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.COBBLESTONE_BRICK_PATH,
                                ModBlocks.COBBLESTONE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.COBBLESTONE_PATH,
                                Blocks.COBBLESTONE, Homestead.MOD_ID);
        }

        /**
         * Registers a path block model, blockstate, and item model using the given
         * block and template block for textures.
         * 
         * @param blockStateModelGenerator The block state model generator
         * @param block                    The mod block to generate files for (e.g.
         *                                 ModBlocks.COBBLESTONE_BRICK_PATH)
         * @param templateBlock            The block to use for textures (e.g.
         *                                 Blocks.COBBLESTONE or
         *                                 ModBlocks.COBBLESTONE_BRICKS)
         * @param modId                    The mod id (e.g. "homestead")
         */
        public static void registerPathBlockModel(BlockStateModelGenerator blockStateModelGenerator,
                        FabricDataOutput dataOutput, Block block,
                        Block templateBlock, String modId) {
                Identifier blockId = Registries.BLOCK.getId(block);
                Identifier templateId = Registries.BLOCK.getId(templateBlock);
                String blockName = blockId.getPath();
                String templateNamespace = templateId.getNamespace();
                String templatePath = templateId.getPath();

                // Generate the model JSON from the template
                Path outputRoot = dataOutput.getPath();

                String texturePath = templateNamespace + ":block/" + templatePath;
                generatePathBlockModelJson(outputRoot, blockName, texturePath, modId);

                // Blockstate JSON (singleton)
                Identifier modelId = Identifier.of(modId, "block/" + blockName);
                blockStateModelGenerator.blockStateCollector.accept(
                                BlockStateModelGenerator.createSingletonBlockState(block, modelId));

                // Item model JSON (parented to block model)
                blockStateModelGenerator.registerParentedItemModel(block, modelId);
        }

        /**
         * Helper to generate a path block model JSON from a template, replacing
         * {texture}.
         * 
         * @param outputRoot  The root output directory for generated assets
         * @param blockName   The name of the block (e.g. "cobblestone_path")
         * @param texturePath The texture path to use (e.g. "minecraft:block/stone")
         * @param modId       The mod id (e.g. "homestead")
         */
        public static void generatePathBlockModelJson(Path outputRoot, String blockName, String texturePath,
                        String modId) {
                // Debug: print the absolute path for src/main/resources and the template file
                Path resourcesPath = Path.of("src/main/resources");
                // Use absolute path from project root for template
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path template = projectRoot
                                .resolve("src/main/resources/assets/" + modId + "/templates/block/path_block.json");
                System.out.println("[DEBUG] src/main/resources absolute path: " + resourcesPath.toAbsolutePath());
                System.out.println("[DEBUG] template file absolute path: " + template.toAbsolutePath());
                try {
                        System.out.println("--- File tree for src/main/resources ---");
                        Files.walk(resourcesPath)
                                        .forEach(p -> System.out.println(p.toString()));
                        System.out.println("--- End file tree ---");
                } catch (IOException e) {
                        System.out.println("[DEBUG] Could not print file tree: " + e.getMessage());
                }

                Path output = projectRoot
                                .resolve("src/generated/assets/" + modId + "/models/block/" + blockName + ".json");
                try {
                        String json = Files.readString(template, StandardCharsets.UTF_8);
                        json = json.replace("{texture}", texturePath);
                        Files.createDirectories(output.getParent());
                        Files.writeString(output, json, StandardCharsets.UTF_8);
                } catch (IOException e) {
                        throw new RuntimeException("Failed to generate path block model for " + blockName, e);
                }
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
