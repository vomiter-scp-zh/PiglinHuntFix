package com.vomiter.piglinhuntfix.mixin;

import com.vomiter.piglinhuntfix.PHFHelpers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Piglin.class)
public class PiglinMixin {
    @Inject(method = "canFireProjectileWeapon", at = @At("TAIL"), cancellable = true)
    private void extendCrossbow(ProjectileWeaponItem item, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()) return;
        if(new ItemStack(item).is(
                TagKey.create(
                        ForgeRegistries.ITEMS.getRegistryKey(),
                        PHFHelpers.id("crossbows_piglin_do_not_use")
                        )
        )) return;
        cir.setReturnValue(item instanceof CrossbowItem);
    }
}
