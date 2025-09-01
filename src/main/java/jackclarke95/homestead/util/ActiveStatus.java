package jackclarke95.homestead.util;

import net.minecraft.util.StringIdentifiable;

public enum ActiveStatus implements StringIdentifiable {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String name;

    private ActiveStatus(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
