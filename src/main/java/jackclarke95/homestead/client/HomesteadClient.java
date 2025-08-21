package jackclarke95.homestead.client;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.client.render.RackBlockEntityRenderer;
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
