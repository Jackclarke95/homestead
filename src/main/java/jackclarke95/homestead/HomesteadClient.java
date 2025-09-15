package jackclarke95.homestead;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.renderer.RackBlockEntityRenderer;
import jackclarke95.homestead.client.VerticleSlabPlacementPreview;
import jackclarke95.homestead.network.ModNetworking;
import jackclarke95.homestead.screen.ModScreenHandlers;
import jackclarke95.homestead.screen.custom.CuringVatScreen;
import jackclarke95.homestead.screen.custom.MillScreen;
import jackclarke95.homestead.screen.custom.PressScreen;

@Environment(EnvType.CLIENT)
public class HomesteadClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
                ModBlocks.RACK,
                ModBlocks.HEATED_RACK,
                ModBlocks.SAWDUST,
                ModBlocks.PEBBLE,
                ModBlocks.PEAR_TREE_SAPLING,
                ModBlocks.PLUM_TREE_SAPLING,
                ModBlocks.LEMON_TREE_SAPLING,
                ModBlocks.ORANGE_TREE_SAPLING,
                ModBlocks.APRICOT_TREE_SAPLING,
                ModBlocks.PEACH_TREE_SAPLING);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : 0x48B518;
        },
                ModBlocks.PEAR_TREE_LEAVES,
                ModBlocks.PLUM_TREE_LEAVES,
                ModBlocks.LEMON_TREE_LEAVES,
                ModBlocks.ORANGE_TREE_LEAVES,
                ModBlocks.APRICOT_TREE_LEAVES,
                ModBlocks.PEACH_TREE_LEAVES);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x48B518, ModBlocks.PEAR_TREE_LEAVES);

        BlockEntityRendererFactories.register(ModBlockEntities.RACK_BE, RackBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.HEATED_RACK_BE, RackBlockEntityRenderer::new);

        HandledScreens.register(ModScreenHandlers.CURING_VAT_SCREEN_HANDLER, CuringVatScreen::new);
        HandledScreens.register(ModScreenHandlers.MILL_SCREEN_HANDLER, MillScreen::new);
        HandledScreens.register(ModScreenHandlers.PRESS_SCREEN_HANDLER,
                PressScreen::new);

        VerticleSlabPlacementPreview.register();

        ModNetworking.registerClient();
    }
}
