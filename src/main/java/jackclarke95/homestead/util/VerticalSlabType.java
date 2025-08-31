package jackclarke95.homestead.util;

import net.minecraft.util.StringIdentifiable;

public enum VerticalSlabType implements StringIdentifiable {
    HALF("half"),
    FULL("full");

    private final String name;

    private VerticalSlabType(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
