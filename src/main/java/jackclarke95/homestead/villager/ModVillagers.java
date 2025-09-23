package jackclarke95.homestead.villager;

import com.google.common.collect.ImmutableSet;
import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.block.Block;
import net.minecraft.registry.Registry;

public class ModVillagers {
    public static final RegistryKey<PointOfInterestType> SOWING_BED_POI_KEY = registerPoiKey("arborist_poi");
    public static final PointOfInterestType SOWING_BED_POI = registerPOI("arborist_poi", ModBlocks.SOWING_BED);

    public static final VillagerProfession ARBORIST = registerProfession("arborist", SOWING_BED_POI_KEY);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(Homestead.MOD_ID, name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_FARMER));
    }

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(Identifier.of(Homestead.MOD_ID, name),
                1, 1, block);
    }

    private static RegistryKey<PointOfInterestType> registerPoiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(Homestead.MOD_ID, name));
    }

    public static void registerVillagers() {
        Homestead.LOGGER.info("Registering Mod Villagers for " + Homestead.MOD_ID);
    }

}
