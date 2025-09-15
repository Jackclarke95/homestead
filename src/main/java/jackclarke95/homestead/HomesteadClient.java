package jackclarke95.homestead;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
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
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RACK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HEATED_RACK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SAWDUST, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEBBLE, RenderLayer.getCutout());

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
