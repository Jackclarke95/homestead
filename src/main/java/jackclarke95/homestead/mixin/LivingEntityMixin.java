package jackclarke95.homestead.mixin;

import jackclarke95.homestead.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /**
     * Intercept dropLoot to replace leather drops with raw hide.
     * This is a placeholder; actual drop logic may require a Redirect or further
     * injection.
     */
    @Inject(method = "dropLoot", at = @At("RETURN"))
    private void homestead$replaceLeatherWithRawHide(DamageSource damageSource, boolean causedByPlayer,
            CallbackInfo ci) {
        if (!((Object) this instanceof LivingEntity entity) || entity.getWorld().isClient())
            return;
        // Replace dropped leather items with raw hide in the area around the entity
        var world = entity.getWorld();
        var box = entity.getBoundingBox().expand(1.5);
        var items = world.getEntitiesByClass(
                net.minecraft.entity.ItemEntity.class,
                box,
                e -> e.getStack().getItem() == Items.LEATHER);
        for (var itemEntity : items) {
            var stack = itemEntity.getStack();
            var rawHide = new ItemStack(ModItems.RAW_HIDE, stack.getCount());
            itemEntity.setStack(rawHide);
        }
    }
}
