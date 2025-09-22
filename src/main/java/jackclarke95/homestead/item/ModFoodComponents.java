package jackclarke95.homestead.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent MUG_OF_VODKA = new FoodComponent.Builder().nutrition(1)
            .saturationModifier(0.2f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 60, 0), 0.10f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 120, 0), 0.10f)
            .build();
    public static final FoodComponent SUSPICIOUS_JERKY = new FoodComponent.Builder().nutrition(4)
            .saturationModifier(1.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300), 0.6f)
            .build();
    public static final FoodComponent CHEESE_SLICE = new FoodComponent.Builder().nutrition(2)
            .saturationModifier(0.25f)
            .build();
    public static final FoodComponent BEEF_JERKY = new FoodComponent.Builder().nutrition(5).saturationModifier(7.2f)
            .build();
    public static final FoodComponent PORK_JERKY = new FoodComponent.Builder().nutrition(5).saturationModifier(7.2f)
            .build();
    public static final FoodComponent MUTTON_JERKY = new FoodComponent.Builder().nutrition(4)
            .saturationModifier(5.4f)
            .build();
    public static final FoodComponent CHICKEN_JERKY = new FoodComponent.Builder().nutrition(4)
            .saturationModifier(4.2f)
            .build();
    public static final FoodComponent RABBIT_JERKY = new FoodComponent.Builder().nutrition(4)
            .saturationModifier(4.8f)
            .build();
    public static final FoodComponent COD_JERKY = new FoodComponent.Builder().nutrition(3).saturationModifier(3.2f)
            .build();
    public static final FoodComponent SALMON_JERKY = new FoodComponent.Builder().nutrition(4)
            .saturationModifier(6.0f)
            .build();

    public static final FoodComponent PEAR = new FoodComponent.Builder().nutrition(3).saturationModifier(1f)
            .build();
    public static final FoodComponent PLUM = new FoodComponent.Builder().nutrition(2).saturationModifier(1f)
            .build();
    public static final FoodComponent LEMON = new FoodComponent.Builder().nutrition(1).saturationModifier(1f)
            .build();
    public static final FoodComponent ORANGE = new FoodComponent.Builder().nutrition(2).saturationModifier(1f)
            .build();
    public static final FoodComponent APRICOT = new FoodComponent.Builder().nutrition(2).saturationModifier(1f)
            .build();
    public static final FoodComponent PEACH = new FoodComponent.Builder().nutrition(3).saturationModifier(1f)
            .build();

    public static final FoodComponent BLACKBERRY = new FoodComponent.Builder().nutrition(1).saturationModifier(0.3f)
            .build();
    public static final FoodComponent RASPBERRY = new FoodComponent.Builder().nutrition(1).saturationModifier(0.3f)
            .build();
}
