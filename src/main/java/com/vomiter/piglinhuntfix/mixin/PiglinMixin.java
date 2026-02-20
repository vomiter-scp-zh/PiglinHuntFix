package com.vomiter.piglinhuntfix.mixin;

import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Piglin.class)
public class PiglinMixin {
    @Inject(method = "canFireProjectileWeapon", at = @At("TAIL"), cancellable = true)
    private void extendCrossbow(ProjectileWeaponItem item, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValue()) return;
        cir.setReturnValue(item instanceof CrossbowItem);
    }
}
