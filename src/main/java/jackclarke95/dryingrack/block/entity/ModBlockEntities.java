package jackclarke95.dryingrack.block.entity;

import jackclarke95.dryingrack.Homestead;
import jackclarke95.dryingrack.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<RackBlockEntity> RACK = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Homestead.MOD_ID, "rack"),
            BlockEntityType.Builder.<RackBlockEntity>create(RackBlockEntity::new, ModBlocks.RACK)
                    .build());

    public static void registerBlockEntities() {
        Homestead.LOGGER.info("Registering block entities for " + Homestead.MOD_ID);
    }
}
