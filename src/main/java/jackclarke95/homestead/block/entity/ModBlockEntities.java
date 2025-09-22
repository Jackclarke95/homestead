package jackclarke95.homestead.block.entity;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.custom.CuringVatBlockEntity;
import jackclarke95.homestead.block.entity.custom.HeatedRackBlockEntity;
import jackclarke95.homestead.block.entity.custom.RackBlockEntity;
import jackclarke95.homestead.block.entity.custom.TroughBlockEntity;
import jackclarke95.homestead.block.entity.custom.MillBlockEntity;
import jackclarke95.homestead.block.entity.custom.PressBlockEntity;
import jackclarke95.homestead.block.entity.custom.SowingBedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<RackBlockEntity> RACK_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "rack_be"),
            BlockEntityType.Builder.create((pos, state) -> new RackBlockEntity(pos, state), ModBlocks.RACK)
                    .build());

    public static final BlockEntityType<HeatedRackBlockEntity> HEATED_RACK_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "heated_rack_be"),
            BlockEntityType.Builder.create((pos, state) -> new HeatedRackBlockEntity(pos, state),
                    ModBlocks.HEATED_RACK)
                    .build());

    public static final BlockEntityType<CuringVatBlockEntity> CURING_VAT_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "curing_vat_be"),
            BlockEntityType.Builder.create((pos, state) -> new CuringVatBlockEntity(pos, state),
                    ModBlocks.CURING_VAT)
                    .build());

    public static final BlockEntityType<PressBlockEntity> PRESS_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "press_be"),
            BlockEntityType.Builder
                    .create((pos, state) -> new PressBlockEntity(pos, state), ModBlocks.PRESS)
                    .build());

    public static final BlockEntityType<MillBlockEntity> MILL_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "mill_be"),
            BlockEntityType.Builder.create((pos, state) -> new MillBlockEntity(pos, state), ModBlocks.MILL)
                    .build());

    public static final BlockEntityType<SowingBedBlockEntity> SOWING_BED_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "sowing_bed_be"),
            BlockEntityType.Builder
                    .create((pos, state) -> new SowingBedBlockEntity(pos, state),
                            ModBlocks.SOWING_BED)
                    .build());

    public static final BlockEntityType<TroughBlockEntity> TROUGH_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE, Identifier.of(Homestead.MOD_ID, "trough_be"),
            BlockEntityType.Builder.create((pos, state) -> new TroughBlockEntity(pos, state),
                    ModBlocks.TROUGH)
                    .build());

    public static void registerBlockEntities() {
        Homestead.LOGGER.info("Registering Block Entities for " + Homestead.MOD_ID);
    }
}
