package jackclarke95.dryingrack.block.entity;

import jackclarke95.dryingrack.DryingRack;
import jackclarke95.dryingrack.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<DryingRackBlockEntity> DRYING_RACK = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(DryingRack.MOD_ID, "drying_rack"),
            BlockEntityType.Builder.<DryingRackBlockEntity>create(DryingRackBlockEntity::new, ModBlocks.DRYING_RACK)
                    .build());

    public static void registerBlockEntities() {
        DryingRack.LOGGER.info("Registering block entities for " + DryingRack.MOD_ID);
    }
}
