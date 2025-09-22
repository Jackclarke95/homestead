package jackclarke95.homestead.screen;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.screen.custom.CuringVatScreenHandler;
import jackclarke95.homestead.screen.custom.MillScreenHandler;
import jackclarke95.homestead.screen.custom.PressScreenHandler;
import jackclarke95.homestead.screen.custom.SowingBedScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<CuringVatScreenHandler> CURING_VAT_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER, Identifier.of(Homestead.MOD_ID, "curing_vat_screen_handler"),
            new ExtendedScreenHandlerType<>(CuringVatScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<MillScreenHandler> MILL_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER, Identifier.of(Homestead.MOD_ID, "mill_screen_handler"),
            new ExtendedScreenHandlerType<>(MillScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<PressScreenHandler> PRESS_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER, Identifier.of(Homestead.MOD_ID, "press_screen_handler"),
            new ExtendedScreenHandlerType<>(PressScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<SowingBedScreenHandler> SOWING_BED_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER, Identifier.of(Homestead.MOD_ID, "sowing_bed_screen_handler"),
            new ExtendedScreenHandlerType<>(SowingBedScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        Homestead.LOGGER.info("Registering Screen Handlers for " + Homestead.MOD_ID);
    }
}