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
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.ANDESITE_PATH,
                                Blocks.ANDESITE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_ANDESITE_PATH,
                                Blocks.POLISHED_ANDESITE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.DIORITE_PATH,
                                Blocks.DIORITE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_DIORITE_PATH,
                                Blocks.POLISHED_DIORITE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.GRANITE_PATH,
                                Blocks.GRANITE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_GRANITE_PATH,
                                Blocks.POLISHED_GRANITE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SAND_PATH,
                                Blocks.SAND, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.GRAVEL_PATH,
                                Blocks.GRAVEL, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.OAK_PLANK_PATH,
                                Blocks.OAK_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SPRUCE_PLANK_PATH,
                                Blocks.SPRUCE_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.BIRCH_PLANK_PATH,
                                Blocks.BIRCH_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.JUNGLE_PLANK_PATH,
                                Blocks.JUNGLE_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.ACACIA_PLANK_PATH,
                                Blocks.ACACIA_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.DARK_OAK_PLANK_PATH,
                                Blocks.DARK_OAK_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.MANGROVE_PLANK_PATH,
                                Blocks.MANGROVE_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.CHERRY_PLANK_PATH,
                                Blocks.CHERRY_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.BAMBOO_PLANK_PATH,
                                Blocks.BAMBOO_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.CRIMSON_PLANK_PATH,
                                Blocks.CRIMSON_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.WARPED_PLANK_PATH,
                                Blocks.WARPED_PLANKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.MOSSY_COBBLESTONE_PATH,
                                Blocks.MOSSY_COBBLESTONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.STONE_BRICK_PATH,
                                Blocks.STONE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.MOSSY_STONE_BRICK_PATH,
                                Blocks.MOSSY_STONE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SMOOTH_STONE_PATH,
                                Blocks.SMOOTH_STONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.CHISELED_STONE_BRICK_PATH,
                                Blocks.CHISELED_STONE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.DEEPSLATE_PATH,
                                Blocks.DEEPSLATE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.COBBLED_DEEPSLATE_PATH,
                                Blocks.COBBLED_DEEPSLATE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.DEEPSLATE_BRICK_PATH,
                                Blocks.DEEPSLATE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.DEEPSLATE_TILE_PATH,
                                Blocks.DEEPSLATE_TILES, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_DEEPSLATE_PATH,
                                Blocks.POLISHED_DEEPSLATE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_TUFF_PATH,
                                Blocks.POLISHED_TUFF, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.TUFF_BRICK_PATH,
                                Blocks.TUFF_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.BRICKS_PATH, Blocks.BRICKS,
                                Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.PACKED_MUD_PATH,
                                Blocks.PACKED_MUD, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.MUD_BRICK_PATH,
                                Blocks.MUD_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SMOOTH_SANDSTONE_PATH,
                                Identifier.of("minecraft", "block/sandstone_top"), Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SMOOTH_RED_SANDSTONE_PATH,
                                Identifier.of("minecraft", "block/red_sandstone_top"), Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.PRISMARINE_PATH,
                                Blocks.PRISMARINE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.PRISMARINE_BRICK_PATH,
                                Blocks.PRISMARINE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.DARK_PRISMARINE_PATH,
                                Blocks.DARK_PRISMARINE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.NETHERRACK_PATH,
                                Blocks.NETHERRACK, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.NETHER_BRICK_PATH,
                                Blocks.NETHER_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.CHISELED_NETHER_BRICK_PATH,
                                Blocks.CHISELED_NETHER_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.RED_NETHER_BRICK_PATH,
                                Blocks.RED_NETHER_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SMOOTH_BASALT_PATH,
                                Blocks.SMOOTH_BASALT, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.BLACKSTONE_PATH,
                                Blocks.BLACKSTONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.GILDED_BLACKSTONE_PATH,
                                Blocks.GILDED_BLACKSTONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output,
                                ModBlocks.CHISELED_POLISHED_BLACKSTONE_PATH, Blocks.CHISELED_POLISHED_BLACKSTONE,
                                Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_BLACKSTONE_PATH,
                                Blocks.POLISHED_BLACKSTONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.POLISHED_BLACKSTONE_BRICK_PATH,
                                Blocks.POLISHED_BLACKSTONE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.END_STONE_PATH,
                                Blocks.END_STONE, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.END_STONE_BRICK_PATH,
                                Blocks.END_STONE_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.PURPUR_BLOCK_PATH,
                                Blocks.PURPUR_BLOCK, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.PURPUR_PILLAR_PATH,
                                Blocks.PURPUR_PILLAR, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.QUARTZ_BLOCK_PATH,
                                Identifier.of("minecraft", "block/quartz_block_top"), Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.CHISELED_QUARTZ_BLOCK_PATH,
                                Blocks.CHISELED_QUARTZ_BLOCK, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.QUARTZ_BRICK_PATH,
                                Blocks.QUARTZ_BRICKS, Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.SMOOTH_QUARTZ_BLOCK_PATH,
                                Identifier.of("minecraft", "block/quartz_block_bottom"), Homestead.MOD_ID);
                registerPathBlockModel(blockStateModelGenerator, this.output, ModBlocks.COARSE_DIRT_PATH,
                                Blocks.COARSE_DIRT, Homestead.MOD_ID);

                registerSurfaceLayerConnectingBlockModel(blockStateModelGenerator, this.output, ModBlocks.SAWDUST,
                                ModBlocks.SAWDUST, Homestead.MOD_ID);

                registerGrowableSurfaceLayerBlockModel(blockStateModelGenerator, this.output, ModBlocks.PEBBLE,
                                ModBlocks.PEBBLE, Homestead.MOD_ID);
        }

        public static void registerPathBlockModel(BlockStateModelGenerator blockStateModelGenerator,
                        FabricDataOutput dataOutput, Block block,
                        Block templateBlock, String modId) {
                Identifier blockId = Registries.BLOCK.getId(block);
                Identifier templateId = Registries.BLOCK.getId(templateBlock);
                String blockName = blockId.getPath();
                String templateNamespace = templateId.getNamespace();
                String templatePath = templateId.getPath();

                Path outputRoot = dataOutput.getPath();

                String texturePath = templateNamespace + ":block/" + templatePath;
                generatePathBlockModelJson(outputRoot, blockName, texturePath, modId);

                Identifier modelId = Identifier.of(modId, "block/" + blockName);
                blockStateModelGenerator.blockStateCollector.accept(
                                BlockStateModelGenerator.createSingletonBlockState(block, modelId));

                blockStateModelGenerator.registerParentedItemModel(block, modelId);
        }

        public static void registerPathBlockModel(BlockStateModelGenerator blockStateModelGenerator,
                        FabricDataOutput dataOutput, Block block,
                        Identifier textureIdentifier, String modId) {
                Identifier blockId = Registries.BLOCK.getId(block);
                String blockName = blockId.getPath();

                String texturePath = textureIdentifier.getNamespace() + ":" + textureIdentifier.getPath();

                Path outputRoot = dataOutput.getPath();
                generatePathBlockModelJson(outputRoot, blockName, texturePath, modId);

                Identifier modelId = Identifier.of(modId, "block/" + blockName);
                blockStateModelGenerator.blockStateCollector.accept(
                                BlockStateModelGenerator.createSingletonBlockState(block, modelId));
                blockStateModelGenerator.registerParentedItemModel(block, modelId);
        }

        public static void generatePathBlockModelJson(Path outputRoot, String blockName, String texturePath,
                        String modId) {
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path template = projectRoot
                                .resolve("src/main/resources/assets/" + modId + "/templates/block/path_block.json");

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

        public static void generateSurfaceLayerConnectingBlockModelJson(Path outputRoot, String blockName,
                        String texturePath,
                        String modId) {
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path baseTemplate = projectRoot
                                .resolve("src/main/resources/assets/" + modId
                                                + "/templates/block/surface_layer_base.json");

                Path baseOutput = projectRoot
                                .resolve("src/generated/assets/" + modId + "/models/block/" + blockName + "_base.json");

                try {
                        String baseJson = Files.readString(baseTemplate, StandardCharsets.UTF_8);
                        baseJson = baseJson.replace("{texture}", texturePath);
                        baseJson = baseJson.replace("{particle}", texturePath);
                        Files.createDirectories(baseOutput.getParent());
                        Files.writeString(baseOutput, baseJson, StandardCharsets.UTF_8);

                        String[] dirs = new String[] { "north", "northeast", "east", "southeast", "south",
                                        "southwest", "west", "northwest" };

                        for (String dir : dirs) {
                                Path overlayOutput = projectRoot.resolve(
                                                "src/generated/assets/" + modId + "/models/block/" + blockName
                                                                + "_overlay_" + dir + ".json");

                                String overlayJson = Files.readString(baseTemplate, StandardCharsets.UTF_8);
                                String overlayTexture = modId + ":block/" + blockName + "_" + dir;
                                overlayJson = overlayJson.replace("{texture}", overlayTexture);
                                overlayJson = overlayJson.replace("{particle}", texturePath);

                                Files.writeString(overlayOutput, overlayJson, StandardCharsets.UTF_8);
                        }

                } catch (IOException e) {
                        throw new RuntimeException("Failed to generate surface layer models for " + blockName, e);
                }
        }

        public static void generateSurfaceLayerConnectingBlockStateJson(Path outputRoot, String blockName,
                        String modId) {
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path output = projectRoot
                                .resolve("src/generated/assets/" + modId + "/blockstates/" + blockName + ".json");

                StringBuilder sb = new StringBuilder();
                sb.append("{\n  \"multipart\": [\n");
                // base
                sb.append("    {\n      \"apply\": { \"model\": \"")
                                .append(modId).append(":block/").append(blockName).append("_base\" }\n    },\n");

                String[] dirs = new String[] { "north", "northeast", "east", "southeast", "south",
                                "southwest", "west", "northwest" };

                for (int i = 0; i < dirs.length; i++) {
                        String dir = dirs[i];
                        sb.append("    {\n      \"when\": { \"").append(dir).append("\": \"true\" },\n");
                        sb.append("      \"apply\": { \"model\": \"").append(modId).append(":block/")
                                        .append(blockName).append("_overlay_").append(dir).append("\" }\n    }");
                        if (i < dirs.length - 1)
                                sb.append(",\n");
                        else
                                sb.append("\n");
                }

                sb.append("  ]\n}");

                try {
                        Files.createDirectories(output.getParent());
                        Files.writeString(output, sb.toString(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                        throw new RuntimeException("Failed to generate surface layer blockstate for " + blockName, e);
                }
        }

        public static void registerSurfaceLayerConnectingBlockModel(BlockStateModelGenerator blockStateModelGenerator,
                        FabricDataOutput dataOutput, Block block, Block templateBlock, String modId) {
                Identifier blockId = Registries.BLOCK.getId(block);
                Identifier templateId = Registries.BLOCK.getId(templateBlock);
                String blockName = blockId.getPath();
                String templateNamespace = templateId.getNamespace();
                String templatePath = templateId.getPath();

                Path outputRoot = dataOutput.getPath();

                String texturePath = templateNamespace + ":block/" + templatePath;
                String baseTexturePath = texturePath + "_base";
                generateSurfaceLayerConnectingBlockModelJson(outputRoot, blockName, baseTexturePath, modId);

                generateSurfaceLayerConnectingBlockStateJson(outputRoot, blockName, modId);

                Identifier modelId = Identifier.of(modId, "block/" + blockName);
                blockStateModelGenerator.blockStateCollector.accept(
                                BlockStateModelGenerator.createSingletonBlockState(block, modelId));

        }

        public static void generateSimpleSurfaceLayerBlockModelJson(Path outputRoot, String blockName,
                        String texturePath, String modId) {
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path template = projectRoot
                                .resolve("src/main/resources/assets/" + modId
                                                + "/templates/block/surface_layer_base.json");
                Path output = projectRoot
                                .resolve("src/generated/assets/" + modId + "/models/block/" + blockName + ".json");

                try {
                        String json = Files.readString(template, StandardCharsets.UTF_8);
                        json = json.replace("{texture}", texturePath);
                        json = json.replace("{particle}", texturePath);
                        Files.createDirectories(output.getParent());
                        Files.writeString(output, json, StandardCharsets.UTF_8);
                } catch (IOException e) {
                        throw new RuntimeException("Failed to generate simple surface layer model for " + blockName, e);
                }
        }

        public static void generateGrowableSurfaceLayerBlockModelJson(Path outputRoot, String blockName,
                        String texturePrefix, String modId) {
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path template = projectRoot
                                .resolve("src/main/resources/assets/" + modId
                                                + "/templates/block/surface_layer_base.json");

                try {
                        String templateJson = Files.readString(template, StandardCharsets.UTF_8);

                        for (int count = 1; count <= 4; count++) {
                                Path output = projectRoot
                                                .resolve("src/generated/assets/" + modId + "/models/block/" + blockName
                                                                + "_" + count + ".json");

                                String texturePath = texturePrefix + "_" + count;
                                String json = templateJson.replace("{texture}", texturePath);
                                json = json.replace("{particle}", texturePath);
                                Files.createDirectories(output.getParent());
                                Files.writeString(output, json, StandardCharsets.UTF_8);
                        }
                } catch (IOException e) {
                        throw new RuntimeException("Failed to generate growable surface layer models for " + blockName,
                                        e);
                }
        }

        public static void generateGrowableSurfaceLayerBlockStateJson(Path outputRoot, String blockName, String modId) {
                Path projectRoot = outputRoot.getParent().getParent().getParent();
                Path output = projectRoot
                                .resolve("src/generated/assets/" + modId + "/blockstates/" + blockName + ".json");

                StringBuilder sb = new StringBuilder();
                sb.append("{\n  \"variants\": {\n");

                String[] facings = { "north", "south", "east", "west" };
                int[] rotations = { 0, 180, 90, 270 };

                boolean first = true;
                for (int count = 1; count <= 4; count++) {
                        for (int i = 0; i < facings.length; i++) {
                                if (!first) {
                                        sb.append(",\n");
                                }
                                first = false;

                                sb.append("    \"count=").append(count).append(",facing=").append(facings[i])
                                                .append("\": {\n");
                                sb.append("      \"model\": \"").append(modId).append(":block/").append(blockName)
                                                .append("_")
                                                .append(count).append("\"");

                                if (rotations[i] > 0) {
                                        sb.append(",\n      \"y\": ").append(rotations[i]);
                                }

                                sb.append("\n    }");
                        }
                }

                sb.append("\n  }\n}");

                try {
                        Files.createDirectories(output.getParent());
                        Files.writeString(output, sb.toString(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                        throw new RuntimeException(
                                        "Failed to generate growable surface layer blockstate for " + blockName, e);
                }
        }

        public static void registerSimpleSurfaceLayerBlockModel(BlockStateModelGenerator blockStateModelGenerator,
                        FabricDataOutput dataOutput, Block block, Block templateBlock, String modId) {
                Identifier blockId = Registries.BLOCK.getId(block);
                Identifier templateId = Registries.BLOCK.getId(templateBlock);
                String blockName = blockId.getPath();
                String templateNamespace = templateId.getNamespace();
                String templatePath = templateId.getPath();

                Path outputRoot = dataOutput.getPath();
                String texturePath = templateNamespace + ":block/" + templatePath;
                generateSimpleSurfaceLayerBlockModelJson(outputRoot, blockName, texturePath, modId);

                Identifier modelId = Identifier.of(modId, "block/" + blockName);
                blockStateModelGenerator.blockStateCollector.accept(
                                BlockStateModelGenerator.createSingletonBlockState(block, modelId));
        }

        public static void registerGrowableSurfaceLayerBlockModel(BlockStateModelGenerator blockStateModelGenerator,
                        FabricDataOutput dataOutput, Block block, Block templateBlock, String modId) {
                Identifier blockId = Registries.BLOCK.getId(block);
                Identifier templateId = Registries.BLOCK.getId(templateBlock);
                String blockName = blockId.getPath();
                String templateNamespace = templateId.getNamespace();
                String templatePath = templateId.getPath();

                Path outputRoot = dataOutput.getPath();
                String texturePrefix = templateNamespace + ":block/" + templatePath;
                generateGrowableSurfaceLayerBlockModelJson(outputRoot, blockName, texturePrefix, modId);
                generateGrowableSurfaceLayerBlockStateJson(outputRoot, blockName, modId);
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
                itemModelGenerator.register(ModBlocks.SAWDUST.asItem(), Models.GENERATED);
                itemModelGenerator.register(ModBlocks.PEBBLE.asItem(), Models.GENERATED);
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
