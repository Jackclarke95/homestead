package jackclarke95.dryingrack.client;

import jackclarke95.dryingrack.DryingRack;
import jackclarke95.dryingrack.block.entity.ModBlockEntities;
import jackclarke95.dryingrack.client.render.DryingRackBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class DryingRackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DryingRack.LOGGER.info("Registering DryingRack block entity renderer");
        BlockEntityRendererFactories.register(ModBlockEntities.DRYING_RACK, DryingRackBlockEntityRenderer::new);
        DryingRack.LOGGER.info("DryingRack block entity renderer registered successfully");
    }
}
