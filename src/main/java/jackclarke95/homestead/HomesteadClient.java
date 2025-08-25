package jackclarke95.homestead;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.block.renderer.RackBlockEntityRenderer;

@Environment(EnvType.CLIENT)
public class HomesteadClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RACK, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.RACK_BE, RackBlockEntityRenderer::new);
    }
}
