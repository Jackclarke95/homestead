package jackclarke95.homestead.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent SUSPICIOUS_JERKY = new FoodComponent.Builder().nutrition(4)
            .saturationModifier(1.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300), 0.6f)
            .build();
    public static final FoodComponent CHEESE_SLICE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.25f)
            .build();
    public static final FoodComponent BEEF_JERKY = new FoodComponent.Builder().nutrition(5).saturationModifier(7.8f)
            .build();

}
