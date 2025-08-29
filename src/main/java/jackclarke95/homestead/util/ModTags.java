package jackclarke95.homestead.util;

import jackclarke95.homestead.Homestead;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
    }

    public static class Items {
        public static final TagKey<Item> VEGETABLES = createTag("vegetables");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Homestead.MOD_ID, name));
        }
    }
}