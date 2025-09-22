package jackclarke95.homestead.item;

import net.minecraft.item.Item;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class FertiliserRegistry {
    private static final Map<Item, Integer> MAP = new HashMap<>();

    private FertiliserRegistry() {
    }

    public static void register(Item item, int units) {
        MAP.put(item, units);
    }

    public static int getUnits(Item item) {
        return MAP.getOrDefault(item, 0);
    }

    public static boolean isFertiliser(Item item) {
        return MAP.containsKey(item);
    }

    public static Map<Item, Integer> getAll() {
        return Collections.unmodifiableMap(MAP);
    }
}
