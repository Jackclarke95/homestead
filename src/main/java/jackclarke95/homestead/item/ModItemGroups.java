package jackclarke95.homestead.item;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup HOMESTEAD_ITEMS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Homestead.MOD_ID, "homestead_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.CHEESE_SLICE))
                    .displayName(Text.translatable("itemGroup.homestead_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.SUSPICIOUS_JERKY);
                        entries.add(ModItems.BEEF_JERKY);
                        entries.add(ModItems.CHEESE_WHEEL);
                        entries.add(ModItems.CHEESE_SLICE);
                        entries.add(ModItems.RAW_HIDE);
                    })
                    .build());

    public static final ItemGroup HOMESTEAD_BLOCKs = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Homestead.MOD_ID, "homestead_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.CURING_VAT))
                    .displayName(Text.translatable("itemGroup.homestead_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.CURING_VAT);
                        entries.add(ModBlocks.CUSTOM_BLOCK);
                        entries.add(ModBlocks.RACK);
                    })
                    .build());

    public static void registerItemGroups() {
        Homestead.LOGGER.info("Registering item groups for " + Homestead.MOD_ID);
    }

}
