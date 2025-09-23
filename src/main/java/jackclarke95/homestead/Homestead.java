package jackclarke95.homestead;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.entity.ModBlockEntities;
import jackclarke95.homestead.event.ModClientEvents;
import jackclarke95.homestead.event.ShearFlowerEventHandler;
import jackclarke95.homestead.item.ModItemGroups;
import jackclarke95.homestead.item.ModItems;
import jackclarke95.homestead.network.ModNetworking;
import jackclarke95.homestead.recipe.ModRecipes;
import jackclarke95.homestead.screen.ModScreenHandlers;
import jackclarke95.homestead.villager.ModVillagers;
import jackclarke95.homestead.world.gen.ModWorldGeneration;

public class Homestead implements ModInitializer {
    public static final String MOD_ID = "homestead";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();

        ModScreenHandlers.registerScreenHandlers();

        ModRecipes.registerRecipes();

        ModWorldGeneration.generateModWorldGen();

        ModNetworking.registerCommon();

        ModVillagers.registerVillagers();
        ModVillagers.registerVillagerTrades();

        ModClientEvents.registerBotanistMapUse();
        ShearFlowerEventHandler.register();
    }
}