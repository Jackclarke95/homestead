package jackclarke95.dryingrack.client;

import jackclarke95.dryingrack.Homestead;
import jackclarke95.dryingrack.block.entity.ModBlockEntities;
import jackclarke95.dryingrack.client.render.RackBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class HomesteadClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Homestead.LOGGER.info("Registering Rack block entity renderer");
        BlockEntityRendererFactories.register(ModBlockEntities.RACK, RackBlockEntityRenderer::new);
        Homestead.LOGGER.info("Rack block entity renderer registered successfully");
    }
}
