package jackclarke95.homestead.util;

import jackclarke95.homestead.Homestead;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class BlockTags {
    }

    public static class ItemTags {
        public static final TagKey<Item> ROOT_VEGETABLES = createTag("root_vegetables");
        public static final TagKey<Item> CROP_SEEDS = createTag("crop_seeds");
        public static final TagKey<Item> FRUITS = createTag("fruits");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Homestead.MOD_ID, name));
        }
    }
}