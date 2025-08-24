package jackclarke95.homestead.block.entity;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.custom.RackBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<RackBlockEntity> RACK_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "rack_be"),
            BlockEntityType.Builder
                    .<RackBlockEntity>create((pos, state) -> new RackBlockEntity(pos, state), ModBlocks.RACK)
                    .build(null));

    public static void registerBlockEntities() {
        Homestead.LOGGER.info("Registering Block Entities for " + Homestead.MOD_ID);
    }
}
